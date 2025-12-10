package co.unicauca.comunicacionmicroservicios.domain.services;

import co.unicauca.comunicacionmicroservicios.domain.model.*;
import co.unicauca.comunicacionmicroservicios.domain.ports.out.clients.IIdentityClientPort;
import co.unicauca.comunicacionmicroservicios.domain.ports.out.events.INotificationPublisherPort;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.db.repository.IAnteproyectoRepository;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.db.repository.IFormatoARepository;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.db.repository.IProyectoGradoRepository;
import co.unicauca.comunicacionmicroservicios.domain.state.ProjectStateFactory;
import co.unicauca.comunicacionmicroservicios.application.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de submission adaptado al dominio rico (ProjectState / ProyectoGrado).
 *
 * Notas:
 * - Asume que ProyectoGrado se construye con (titulo, ProjectStateFactory).
 * - No intenta escribir campos inexistentes en ProyectoGrado; esos metadatos se guardan en FormatoA/Anteproyecto.
 */
@Service
public class SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);

    private final IProyectoGradoRepository proyectoRepo;
    private final IFormatoARepository formatoRepo;
    private final IAnteproyectoRepository anteproyectoRepo;
    private final INotificationPublisherPort notificationPublisher;
    private final IIdentityClientPort identityClient;
    private final ProjectStateFactory stateFactory;

    @Value("${file.storage.path:/app/uploads}")
    private String storageBasePath;

    public SubmissionService(IProyectoGradoRepository proyectoRepo,
                             IFormatoARepository formatoRepo,
                             IAnteproyectoRepository anteproyectoRepo,
                             INotificationPublisherPort notificationPublisher,
                             IIdentityClientPort identityClient,
                             ProjectStateFactory stateFactory) {
        this.proyectoRepo = proyectoRepo;
        this.formatoRepo = formatoRepo;
        this.anteproyectoRepo = anteproyectoRepo;
        this.notificationPublisher = notificationPublisher;
        this.identityClient = identityClient;
        this.stateFactory = stateFactory;
    }

    // ---------------------------
    // RF2: Crear Formato A v1
    // ---------------------------
    @Transactional
    public IdResponseDTO crearFormatoA(String userId, FormatoADataDTO data, MultipartFile pdf, MultipartFile carta) {
        validarArchivoPdfObligatorio(pdf);
        validarCartaSiPractica(data, carta);

        // 1) Crear el agregado raíz ProyectoGrado
        ProyectoGrado proyecto = new ProyectoGrado(data.getTitulo(), stateFactory);
        proyecto = proyectoRepo.save(proyecto);

        // 2) Guardar archivos en almacenamiento
        String base = "formato-a/" + proyecto.getId() + "/v1";
        String pdfPath = guardarArchivo(base, "documento.pdf", pdf);
        String cartaPath = (data.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL)
                ? guardarArchivo(base, "carta.pdf", carta)
                : null;

        // 3) Crear entidad FormatoA con metadatos
        FormatoA v1 = new FormatoA();
        v1.setProyecto(proyecto);
        v1.setNumeroIntento(1);
        v1.setRutaArchivo(pdfPath);
        v1.setNombreArchivo(Optional.ofNullable(pdf.getOriginalFilename()).orElse("documento.pdf"));
        v1.setFechaCarga(LocalDateTime.now());
        if (cartaPath != null) {
            v1.setRutaCartaAceptacion(cartaPath);
            v1.setNombreCartaAceptacion(Optional.ofNullable(carta.getOriginalFilename()).orElse("carta.pdf"));
        }
        v1.setEstado(enumEstadoFormato.PENDIENTE);
        formatoRepo.save(v1);

        String coordinadorEmail = "coordinador@unicauca.edu.co";
        String submittedByName = "Docente " + userId;

        Map<String, Object> businessContext = new HashMap<>();
        businessContext.put("projectId", proyecto.getId());
        businessContext.put("version", 1);
        businessContext.put("submittedBy", submittedByName);
        businessContext.put("projectTitle", proyecto.getTitulo());

        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.FORMATO_A_PRESENTADO)
                .subject("Nuevo Formato A Presentado")
                .message("Se ha presentado un nuevo Formato A v1 para el proyecto: " + proyecto.getTitulo() +
                        " por: " + submittedByName)
                .recipients(List.of(
                        Recipient.builder()
                                .email(coordinadorEmail)
                                .role("COORDINATOR")
                                .build()
                ))
                .businessContext(businessContext) // ✅ Usar el HashMap
                .channel("email")
                .build();

        notificationPublisher.publishNotification(notificacion, "Formato A enviado v1");

        log.info("Formato A v1 creado para proyecto {} - Notificación enviada al coordinador: {}",
                proyecto.getId(), coordinadorEmail);

        return new IdResponseDTO(parseLongSafeStringId(String.valueOf(proyecto.getId())));
    }

    // ---------------------------
    // Lecturas de Formato A
    // ---------------------------
    @Transactional(readOnly = true)
    public FormatoAViewDTO obtenerFormatoA(Long id) {
        FormatoA fa = formatoRepo.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formato A no encontrado"));

        FormatoAViewDTO view = new FormatoAViewDTO();
        view.setId(fa.getId().longValue());
        view.setProyectoId(fa.getProyecto() != null ? safeParseLong(String.valueOf(fa.getProyecto().getId())) : null);
        view.setVersion(fa.getNumeroIntento());
        view.setEstado(fa.getEstado());
        view.setObservaciones(fa.getObservaciones());
        view.setNombreArchivo(fa.getNombreArchivo());
        view.setPdfUrl(fa.getRutaArchivo());
        view.setCartaUrl(fa.getRutaCartaAceptacion());
        view.setFechaEnvio(fa.getFechaCarga());
        return view;
    }

    @Transactional(readOnly = true)
    public FormatoAPageDTO listarFormatoA(Optional<String> docenteId, int page, int size) {
        List<FormatoAViewDTO> content = formatoRepo.findAll().stream()
                .filter(fa -> {
                    if (docenteId.isEmpty()) return true;
                    // Si FormatoA guarda directorId como Integer, comparamos con seguridad
                    try {
                        Integer dirId = Integer.parseInt(docenteId.get());
                        return Objects.equals(fa.getProyecto(), dirId);
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .sorted(Comparator.comparing(FormatoA::getFechaCarga).reversed())
                .limit(size)
                .map(fa -> {
                    FormatoAViewDTO v = new FormatoAViewDTO();
                    v.setId(fa.getId().longValue());
                    v.setProyectoId(fa.getProyecto() != null ? safeParseLong(String.valueOf(fa.getProyecto().getId())) : null);
                    v.setVersion(fa.getNumeroIntento());
                    v.setEstado(fa.getEstado());
                    v.setNombreArchivo(fa.getNombreArchivo());
                    v.setPdfUrl(fa.getRutaArchivo());
                    v.setCartaUrl(fa.getRutaCartaAceptacion());
                    v.setFechaEnvio(fa.getFechaCarga());
                    return v;
                }).collect(Collectors.toList());

        FormatoAPageDTO res = new FormatoAPageDTO();
        res.setContent(content);
        res.setPage(page);
        res.setSize(size);
        res.setTotalElements(content.size());
        return res;
    }

    // ---------------------------
    // RF4: Reenviar Formato A
    // ---------------------------
    @Transactional
    public IdResponseDTO reenviarFormatoA(String userId, Long proyectoId, MultipartFile pdf, MultipartFile carta) {
        validarArchivoPdfObligatorio(pdf);

        // CORRECCIÓN: Convertir Long a Integer
        Integer proyectoIdInt;
        try {
            proyectoIdInt = proyectoId.intValue();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID de proyecto inválido: " + proyectoId);
        }

        // Buscamos proyecto por id (convertido a Integer)
        ProyectoGrado proyecto = proyectoRepo.findById(proyectoIdInt)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe"));

        // Preguntamos al dominio si permite reenvío
        if (!proyecto.permiteReenvioFormatoA(stateFactory)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El proyecto no permite reenvío de Formato A");
        }

        // Incrementar intentos en el dominio
        proyecto.incrementarIntentos();
        proyecto = proyectoRepo.save(proyecto);

        int next = proyecto.getIntentosFormatoA();

        // Guardar archivos
        String base = "formato-a/" + proyecto.getId() + "/v" + next;
        String pdfPath = guardarArchivo(base, "documento.pdf", pdf);
        String cartaPath = carta != null && !carta.isEmpty()
                ? guardarArchivo(base, "carta.pdf", carta)
                : null;

        FormatoA nueva = new FormatoA();
        nueva.setProyecto(proyecto);
        nueva.setNumeroIntento(next);
        nueva.setRutaArchivo(pdfPath);
        nueva.setNombreArchivo(Optional.ofNullable(pdf.getOriginalFilename()).orElse("documento.pdf"));
        nueva.setFechaCarga(LocalDateTime.now());
        if (cartaPath != null) {
            nueva.setRutaCartaAceptacion(cartaPath);
            nueva.setNombreCartaAceptacion(Optional.ofNullable(carta.getOriginalFilename()).orElse("carta.pdf"));
        }
        nueva.setEstado(enumEstadoFormato.PENDIENTE);
        formatoRepo.save(nueva);

        // Delegar evento al dominio
        proyecto.manejarFormatoA(stateFactory, "Formato A reenviado v" + next);
        proyecto = proyectoRepo.save(proyecto);

        // Notificar usando el método publishNotification directamente
        String coordinadorEmail = identityClient.getCoordinadorEmail();
        String submittedByName = identityClient.getUserName(userId);
        Integer proyectoIdForNotification = parseIntSafeStringId(String.valueOf(proyecto.getId()));

        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.FORMATO_A_REENVIADO)
                .subject("Formato A Presentado")
                .message("Se ha presentado el Formato A v" + next + " para el proyecto: " + proyecto.getTitulo() +
                        " por: " + submittedByName)
                .recipients(List.of(
                        Recipient.builder()
                                .email(coordinadorEmail)
                                .role("COORDINATOR")
                                .build()
                ))
                .businessContext(Map.of(
                        "projectId", proyectoIdForNotification,
                        "version", next,
                        "submittedBy", submittedByName,
                        "projectTitle", proyecto.getTitulo()
                ))
                .channel("email")
                .build();

        notificationPublisher.publishNotification(notificacion, "Formato A enviado v" + next);

        log.info("Formato A v{} reenviado para proyecto {} - Notificación enviada al coordinador: {}",
                next, proyecto.getId(), coordinadorEmail);

        return new IdResponseDTO(parseLongSafeStringId(String.valueOf(proyecto.getId())));
    }

    // ------------------------------------------
    // RF3: Cambiar estado de una versión (por Review/Coordinador)
    // ------------------------------------------
    @Transactional
    public void cambiarEstadoFormatoA(Long versionId, EvaluacionRequestDTO req) {
        FormatoA formato = formatoRepo.findById(versionId.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Versión de Formato A no existe"));

        ProyectoGrado proyecto = formato.getProyecto();

        // Delegamos la evaluación al agregado raíz (manejo del estado)
        proyecto.evaluarFormatoA(stateFactory, req.getEstado().name(), req.getObservaciones());
        proyecto = proyectoRepo.save(proyecto);

        // Actualizamos la entidad FormatoA
        if (req.getEstado() == enumEstadoFormato.APROBADO) {
            formato.aprobar(req.getEvaluadoPor(), req.getObservaciones());
        } else if (req.getEstado() == enumEstadoFormato.RECHAZADO) {
            formato.rechazar(req.getEvaluadoPor(), req.getObservaciones());
        }
        formatoRepo.save(formato);

        // Si el dominio derivó a rechazo definitivo (no permite más reenvíos)
        if (!proyecto.permiteReenvioFormatoA(stateFactory)) {
            notificationPublisher.notificarRechazoDefinitivoFormatoA(proyecto);
        }
    }

    // ---------------------------
    // RF6: Subir Anteproyecto
    // ---------------------------
    @Transactional
    public IdResponseDTO subirAnteproyecto(String userId, AnteproyectoDataDTO data, MultipartFile pdf) {
        validarArchivoPdfObligatorio(pdf);

        // CORRECCIÓN: Convertir de Long a Integer
        Integer proyectoId;
        try {
            // Si data.getProyectoId() es Long, conviértelo a Integer
            proyectoId = data.getProyectoId().intValue();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID de proyecto no puede ser nulo");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID de proyecto inválido: " + data.getProyectoId());
        }

        ProyectoGrado proyecto = proyectoRepo.findById(proyectoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe"));

        // Delegar verificación de permisos al dominio si implementado
        if (!proyecto.permiteSubirAnteproyecto(stateFactory)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No puede subir anteproyecto en el estado actual");
        }

        // Guardar archivo
        String base = "anteproyectos/" + proyecto.getId();
        String pdfPath = guardarArchivo(base, "documento.pdf", pdf);

        Anteproyecto ant = new Anteproyecto();
        ant.setProyecto(proyecto);
        ant.setRutaArchivo(pdfPath);
        ant.setNombreArchivo(Optional.ofNullable(pdf.getOriginalFilename()).orElse("documento.pdf"));
        ant.setFechaEnvio(LocalDateTime.now());
        anteproyectoRepo.save(ant);

        // Delegar al dominio
        proyecto.manejarAnteproyecto(stateFactory, "Anteproyecto presentado");
        proyecto = proyectoRepo.save(proyecto);

        // Notificar jefe de departamento
        String jefeDepartamentoEmail = identityClient.getJefeDepartamentoEmail();
        String submittedByName = identityClient.getUserName(userId);

        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.ANTEPROYECTO_PRESENTADO)
                .subject("Nuevo Anteproyecto Presentado")
                .message("Se ha presentado un nuevo anteproyecto para el proyecto: " + proyecto.getTitulo() +
                        " por el estudiante: " + submittedByName)
                .recipients(List.of(
                        Recipient.builder()
                                .email(jefeDepartamentoEmail)
                                .role("DEPARTMENT_HEAD")
                                .build()
                ))
                .businessContext(Map.of(
                        "projectId", proyecto.getId(), // Ya es Integer, no necesita conversión
                        "submittedBy", submittedByName,
                        "projectTitle", proyecto.getTitulo()
                ))
                .channel("email")
                .build();

        notificationPublisher.publishNotification(notificacion, "Anteproyecto presentado");

        return new IdResponseDTO(parseLongSafeStringId(ant.getId().toString()));
    }

    // ---------------------------
    // Lectura de Anteproyecto por ID (NUEVO)
    // ---------------------------
    @Transactional(readOnly = true)
    public AnteproyectoViewDTO obtenerAnteproyecto(Long id) {
        Anteproyecto ant = anteproyectoRepo.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anteproyecto no encontrado"));

        // Se usa AnteproyectoViewDTO ya que es el DTO de vista existente.
        AnteproyectoViewDTO view = new AnteproyectoViewDTO();
        view.setId(ant.getId().longValue());
        view.setProyectoId(ant.getProyecto() != null ? safeParseLong(String.valueOf(ant.getProyecto().getId())) : null);
        view.setPdfUrl(ant.getRutaArchivo());
        view.setFechaEnvio(ant.getFechaEnvio());
        view.setEstado(ant.getEstado());
        // Aquí podrías añadir más información del proyecto (ej. título) si es necesario en el DTO

        return view;
    }

    @Transactional(readOnly = true)
    public AnteproyectoPageDTO listarAnteproyectos(int page, int size) {
        var content = anteproyectoRepo.findAll().stream()
                .sorted(Comparator.comparing(Anteproyecto::getFechaEnvio).reversed())
                .limit(size)
                .map(a -> {
                    AnteproyectoViewDTO v = new AnteproyectoViewDTO();
                    v.setId(a.getId().longValue());
                    v.setProyectoId(a.getProyecto() != null ? safeParseLong(String.valueOf(a.getProyecto().getId())) : null);
                    v.setPdfUrl(a.getRutaArchivo());
                    v.setFechaEnvio(a.getFechaEnvio());
                    v.setEstado(a.getEstado());
                    return v;
                }).collect(Collectors.toList());

        AnteproyectoPageDTO pageRes = new AnteproyectoPageDTO();
        pageRes.setContent(content);
        pageRes.setPage(page);
        pageRes.setSize(size);
        pageRes.setTotalElements(content.size());
        return pageRes;
    }

    @Transactional
    public void cambiarEstadoAnteproyecto(Long id, CambioEstadoAnteproyectoRequestDTO req) {
        Anteproyecto ant = anteproyectoRepo.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anteproyecto no existe"));
        ant.setEstado(req.getEstado());
        anteproyectoRepo.save(ant);
    }

    // ---------------------------
    // Utilidades (helpers pequeños)
    // ---------------------------
    private void validarArchivoPdfObligatorio(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo PDF obligatorio");
        }
        if (!Objects.equals(file.getContentType(), "application/pdf")) {
            if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser PDF");
            }
        }
    }

    private void validarCartaSiPractica(FormatoADataDTO data, MultipartFile carta) {
        if (data.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL) {
            if (carta == null || carta.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Carta de aceptación obligatoria en Práctica Profesional");
            }
        }
    }

    private String guardarArchivo(String relativeDir, String fileName, MultipartFile file) {
        try {
            Path base = Paths.get(storageBasePath);
            Path dir = base.resolve(relativeDir).normalize();
            Files.createDirectories(dir);
            Path full = dir.resolve(fileName).normalize();
            Files.write(full, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return full.toString();
        } catch (IOException e) {
            log.error("Error guardando archivo", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el archivo");
        }
    }

    private Integer parseIntSafeStringId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {
            return null; // si no es convertible, devolvemos null y el publisher debe soportarlo
        }
    }

    private Long parseLongSafeStringId(String id) {
        try {
            return Long.parseLong(id);
        } catch (Exception e) {
            return null;
        }
    }

    private Long safeParseLong(String id) {
        try { return id == null ? null : Long.parseLong(id); } catch (Exception e) { return null; }
    }
}
