package co.unicauca.comunicacionmicroservicios.service;

import co.unicauca.comunicacionmicroservicios.domain.model.*;
import co.unicauca.comunicacionmicroservicios.domain.model.state.IEstadoProyecto;
import co.unicauca.comunicacionmicroservicios.dto.*;
import co.unicauca.comunicacionmicroservicios.infraestructure.repository.*;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class SubmissionService implements ISubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);

    private final IProyectoGradoRepository proyectoRepo;
    private final IFormatoARepository formatoRepo;
    private final IAnteproyectoRepository anteproyectoRepo;

    private final SubmissionPublisher publisher;
    private final NotificationClient notificationClient;

    @Value("${file.storage.path:/app/uploads}")
    private String storageBasePath;

    // ---------------------------
    // RF2: Crear Formato A v1
    // ---------------------------
    @Override
    @Transactional
    public IdResponse crearFormatoA(String userId, FormatoAData data, MultipartFile pdf, MultipartFile carta) {
        validarArchivoPdfObligatorio(pdf);
        validarCartaSiPractica(data, carta);

        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setTitulo(data.getTitulo());
        proyecto.setModalidad(data.getModalidad());
        proyecto.setDirectorId(data.getDirectorId());
        proyecto.setCodirectorId(data.getCodirectorId());
        proyecto.setObjetivoGeneral(data.getObjetivoGeneral());
        proyecto.setObjetivosEspecificos(String.join("\n",
                data.getObjetivosEspecificos() != null ? data.getObjetivosEspecificos() : List.of()));
        proyecto.setEstudiante1Id(data.getEstudiante1Id());
        proyecto.setEstudiante2Id(data.getEstudiante2Id());
        proyecto.setNumeroIntentos(1);
        proyecto.setEstado(enumEstadoProyecto.FORMATO_A_DILIGENCIADO);

        proyecto = proyectoRepo.save(proyecto);

        String base = "formato-a/" + proyecto.getId() + "/v1";
        String pdfPath = guardarArchivo(base, "documento.pdf", pdf);
        String cartaPath = (data.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL)
                ? guardarArchivo(base, "carta.pdf", carta)
                : null;

        FormatoA v1 = new FormatoA();
        v1.setProyecto(proyecto);
        v1.setNumeroIntento(1);
        v1.setRutaArchivo(pdfPath);
        v1.setNombreArchivo(pdf.getOriginalFilename() != null ? pdf.getOriginalFilename() : "documento.pdf");
        v1.setFechaCarga(LocalDateTime.now());
        if (cartaPath != null) {
            v1.setRutaCartaAceptacion(cartaPath);
            v1.setNombreCartaAceptacion(carta != null ? carta.getOriginalFilename() : "carta.pdf");
        }
        v1.setEstado(enumEstadoFormato.PENDIENTE);
        formatoRepo.save(v1);

        publisher.publicarFormatoAEnviado(Map.of(
                "proyectoId", proyecto.getId(),
                "version", 1,
                "titulo", proyecto.getTitulo()
        ));

        log.info("Formato A v1 creado para proyecto {}", proyecto.getId());
        return new IdResponse(Long.valueOf(proyecto.getId()));
    }

    // ---------------------------
    // RF4: Reenviar Formato A
    // ---------------------------
    @Override
    @Transactional
    public IdResponse reenviarFormatoA(String userId, Long proyectoId, MultipartFile pdf, MultipartFile carta) {
        validarArchivoPdfObligatorio(pdf);

        ProyectoGrado proyecto = proyectoRepo.findById(proyectoId.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe"));

        if (proyecto.getEstado() == enumEstadoProyecto.RECHAZADO_DEFINITIVO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Proyecto rechazado definitivamente");
        }
        if (proyecto.getEstado() != enumEstadoProyecto.RECHAZADO_POR_COMITE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El proyecto no está en estado de reenvío");
        }
        if (proyecto.getNumeroIntentos() >= 3) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Se alcanzó el máximo de 3 intentos");
        }

        int next = proyecto.getNumeroIntentos() + 1;
        proyecto.setNumeroIntentos(next);
        proyecto.setEstado(enumEstadoProyecto.FORMATO_A_DILIGENCIADO); // Reiniciar estado al reenviar
        proyectoRepo.save(proyecto);

        if (proyecto.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL && (carta == null || carta.isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carta de aceptación obligatoria en Práctica Profesional");
        }

        String base = "formato-a/" + proyecto.getId() + "/v" + next;
        String pdfPath = guardarArchivo(base, "documento.pdf", pdf);
        String cartaPath = (proyecto.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL)
                ? guardarArchivo(base, "carta.pdf", carta)
                : null;

        FormatoA nueva = new FormatoA();
        nueva.setProyecto(proyecto);
        nueva.setNumeroIntento(next);
        nueva.setRutaArchivo(pdfPath);
        nueva.setNombreArchivo(pdf.getOriginalFilename() != null ? pdf.getOriginalFilename() : "documento.pdf");
        nueva.setFechaCarga(LocalDateTime.now());
        if (cartaPath != null) {
            nueva.setRutaCartaAceptacion(cartaPath);
            nueva.setNombreCartaAceptacion(carta.getOriginalFilename());
        }
        nueva.setEstado(enumEstadoFormato.PENDIENTE);
        formatoRepo.save(nueva);

        publisher.publicarFormatoAReenviado(Map.of(
                "proyectoId", proyecto.getId(),
                "version", next,
                "titulo", proyecto.getTitulo()
        ));

        return new IdResponse(Long.valueOf(proyecto.getId()));
    }

    // ------------------------------------------
    // Cambiar estado de una versión (por Review)
    // ------------------------------------------
    @Override
    @Transactional
    public void cambiarEstadoFormatoA(Long versionId, EvaluacionRequest req) {
        FormatoA formato = formatoRepo.findById(versionId.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Versión de Formato A no existe"));
        ProyectoGrado proyecto = formato.getProyecto();

        IEstadoProyecto estado = proyecto.obtenerEstadoActual();

        if (req.getEstado() == enumEstadoFormato.APROBADO) {
            formato.aprobar(req.getEvaluadoPor(), req.getObservaciones());
            estado.aprobar(proyecto);
        }else if (req.getEstado() == enumEstadoFormato.RECHAZADO) {
            formato.rechazar(req.getEvaluadoPor(), req.getObservaciones());
            estado.rechazar(proyecto);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido para evaluación");
        }

        formatoRepo.save(formato);
        proyectoRepo.save(proyecto);

        // Notificación si es rechazo definitivo
        if (proyecto.getEstado() == enumEstadoProyecto.RECHAZADO_DEFINITIVO) {
            publisher.publicarProyectoRechazoDefinitivo(Map.of(
                    "proyectoId", proyecto.getId(),
                    "titulo", proyecto.getTitulo()
            ));
        }
    }

    // ---------------------------
    // RF6: Subir Anteproyecto
    // ---------------------------
    @Override
    @Transactional
    public IdResponse subirAnteproyecto(String userId, AnteproyectoData data, MultipartFile pdf) {
        validarArchivoPdfObligatorio(pdf);

        ProyectoGrado proyecto = proyectoRepo.findById(data.getProyectoId().intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe"));

        if (proyecto.getDirectorId() == null || !proyecto.getDirectorId().toString().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el director puede subir el anteproyecto");
        }

        if (proyecto.getEstado() != enumEstadoProyecto.ACEPTADO_POR_COMITE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Formato A no está aprobado por el comité");
        }

        anteproyectoRepo.findByProyecto(proyecto).ifPresent(a -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un anteproyecto para este proyecto");
        });

        String base = "anteproyectos/" + proyecto.getId();
        String pdfPath = guardarArchivo(base, "documento.pdf", pdf);

        Anteproyecto ant = new Anteproyecto();
        ant.setProyecto(proyecto);
        ant.setRutaArchivo(pdfPath);
        ant.setNombreArchivo(pdf.getOriginalFilename() != null ? pdf.getOriginalFilename() : "documento.pdf");
        ant.setFechaEnvio(LocalDateTime.now());
        anteproyectoRepo.save(ant);

        // actualizar estado del proyecto
        proyecto.setEstado(enumEstadoProyecto.PRESENTADO_JEFATURA);
        proyectoRepo.save(proyecto);

        publisher.publicarAnteproyectoEnviado(Map.of(
                "proyectoId", proyecto.getId(),
                "titulo", proyecto.getTitulo()
        ));

        return new IdResponse(Long.valueOf(ant.getId()));
    }

    // ---------------------------
    // RF7: Listar anteproyectos para jefe
    // ---------------------------
    @Override
    @Transactional(readOnly = true)
    public AnteproyectoPage listarAnteproyectos(int page, int size) {
        var content = anteproyectoRepo.findAll().stream()
                .sorted(Comparator.comparing(Anteproyecto::getFechaEnvio).reversed())
                .limit(size)
                .map(a -> {
                    AnteproyectoView v = new AnteproyectoView();
                    v.setId(Long.valueOf(a.getId()));
                    v.setProyectoId(Long.valueOf(a.getProyecto().getId()));
                    v.setPdfUrl(a.getRutaArchivo());
                    v.setFechaEnvio(a.getFechaEnvio());
                    v.setEstado(a.getEstado());
                    return v;
                }).collect(Collectors.toList());

        AnteproyectoPage pageRes = new AnteproyectoPage();
        pageRes.setContent(content);
        pageRes.setPage(page);
        pageRes.setSize(size);
        pageRes.setTotalElements(content.size());
        return pageRes;
    }

    @Override
    @Transactional
    public void cambiarEstadoAnteproyecto(Long id, CambioEstadoAnteproyectoRequest req) {
        Anteproyecto ant = anteproyectoRepo.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anteproyecto no existe"));
        ant.setEstado(req.getEstado());
        anteproyectoRepo.save(ant);
    }

    // ---------------------------
    // Métodos de lectura (sin cambios)
    // ---------------------------
    @Override
    @Transactional(readOnly = true)
    public FormatoAView obtenerFormatoA(Long id) {
        FormatoA fa = formatoRepo.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formato A no encontrado"));

        FormatoAView view = new FormatoAView();
        view.setId(Long.valueOf(fa.getId()));
        view.setProyectoId(Long.valueOf(fa.getProyecto().getId()));
        view.setVersion(fa.getNumeroIntento());
        view.setEstado(fa.getEstado());
        view.setObservaciones(fa.getObservaciones());
        view.setNombreArchivo(fa.getNombreArchivo());
        view.setPdfUrl(fa.getRutaArchivo());
        view.setCartaUrl(fa.getRutaCartaAceptacion());
        view.setFechaEnvio(fa.getFechaCarga());
        return view;
    }

    @Override
    @Transactional(readOnly = true)
    public FormatoAPage listarFormatoA(Optional<String> docenteId, int page, int size) {
        List<FormatoAView> content = formatoRepo.findAll().stream()
                .filter(fa -> docenteId.isEmpty() ||
                        Objects.equals(fa.getProyecto().getDirectorId(), parseIntSafe(docenteId.get())))
                .sorted(Comparator.comparing(FormatoA::getFechaCarga).reversed())
                .limit(size)
                .map(fa -> {
                    FormatoAView v = new FormatoAView();
                    v.setId(Long.valueOf(fa.getId()));
                    v.setProyectoId(Long.valueOf(fa.getProyecto().getId()));
                    v.setVersion(fa.getNumeroIntento());
                    v.setEstado(fa.getEstado());
                    v.setNombreArchivo(fa.getNombreArchivo());
                    v.setPdfUrl(fa.getRutaArchivo());
                    v.setCartaUrl(fa.getRutaCartaAceptacion());
                    v.setFechaEnvio(fa.getFechaCarga());
                    return v;
                }).collect(Collectors.toList());

        FormatoAPage res = new FormatoAPage();
        res.setContent(content);
        res.setPage(page);
        res.setSize(size);
        res.setTotalElements(content.size());
        return res;
    }

    // ---------------------------
    // Utilidades
    // ---------------------------
    private void validarArchivoPdfObligatorio(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo PDF obligatorio");
        }
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser PDF");
        }
    }

    private void validarCartaSiPractica(FormatoAData data, MultipartFile carta) {
        if (data.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL) {
            if (carta == null || carta.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Carta de aceptación de la empresa es obligatoria en Práctica Profesional");
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

    private Integer parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }
}