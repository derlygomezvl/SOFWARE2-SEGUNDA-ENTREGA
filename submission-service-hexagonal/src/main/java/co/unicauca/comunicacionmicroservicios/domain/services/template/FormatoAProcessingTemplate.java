package co.unicauca.comunicacionmicroservicios.domain.services.template;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.domain.state.ProjectStateFactory;
import co.unicauca.comunicacionmicroservicios.application.dto.NotificationRequest;
import co.unicauca.comunicacionmicroservicios.application.dto.NotificationType;
import co.unicauca.comunicacionmicroservicios.application.dto.Recipient;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.events.NotificationPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Implementación concreta del Template Method para procesar Formato A
 */
@Component
public class FormatoAProcessingTemplate extends DocumentProcessingTemplate {

    private final ProjectStateFactory stateFactory;

    // ✅ CORREGIDO: Inyectar ProjectStateFactory en el constructor
    public FormatoAProcessingTemplate(NotificationPublisher notificationPublisher,
                                      ProjectStateFactory stateFactory) {
        super(notificationPublisher);
        this.stateFactory = stateFactory;
    }

    @Override
    protected void validarDocumento(DocumentData documentData) {
        logger.info("Validando Formato A...");

        // Validaciones específicas del Formato A
        if (documentData.getContenido() == null || documentData.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del Formato A no puede estar vacío");
        }

        if (documentData.getTitulo() == null || documentData.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del proyecto es obligatorio");
        }

        if (documentData.getModalidad() == null) {
            throw new IllegalArgumentException("La modalidad es obligatoria");
        }

        if (documentData.getObjetivoGeneral() == null || documentData.getObjetivoGeneral().trim().isEmpty()) {
            throw new IllegalArgumentException("El objetivo general es obligatorio");
        }

        // Validar que contenga secciones mínimas
        if (!documentData.getContenido().contains("OBJETIVO_GENERAL") &&
                !documentData.getContenido().toLowerCase().contains("objetivo general")) {
            throw new IllegalArgumentException("El Formato A debe contener una sección de objetivo general");
        }

        // Validar modalidad Práctica Profesional
        if ("PRACTICA_PROFESIONAL".equalsIgnoreCase(documentData.getModalidad()) &&
                (documentData.getArchivoAdjunto() == null || documentData.getArchivoAdjunto().trim().isEmpty())) {
            throw new IllegalArgumentException("Para modalidad Práctica Profesional se requiere carta de aceptación de la empresa");
        }

        logger.info("Formato A validado exitosamente");
    }

    @Override
    protected void validarEstadoProyecto(ProyectoGrado proyecto) {
        logger.info("Validando estado del proyecto para Formato A...");

        // ✅ CORREGIDO: Pasar stateFactory al método de consulta
        if (!proyecto.permiteReenvioFormatoA(stateFactory) && proyecto.getEstado() != ProjectStateEnum.FORMATO_A_PRESENTADO) {
            throw new IllegalStateException(
                    "El proyecto no permite subir Formato A en el estado actual: " +
                            proyecto.getEstado().getDescripcion()
            );
        }

        // Verificar intentos máximos
        if (proyecto.getIntentosFormatoA() >= 3) {
            throw new IllegalStateException("Límite de intentos para Formato A excedido. Proyecto cancelado.");
        }

        logger.info("Estado del proyecto validado para Formato A");
    }

    @Override
    protected String guardarDocumento(ProyectoGrado proyecto, DocumentData documentData) {
        logger.info("Guardando Formato A en repositorio...");

        // Aquí integrarías con tu IFormatoARepository existente
        // Por ahora simulamos el guardado
        String documentId = "FORMATO_A_" + proyecto.getId() + "_" + System.currentTimeMillis();

        // Lógica de guardado específica para Formato A
        logger.info("Formato A guardado con ID: {} para proyecto: {}", documentId, proyecto.getId());

        return documentId;
    }

    @Override
    protected void actualizarEstadoProyecto(ProyectoGrado proyecto) {
        logger.info("Actualizando estado del proyecto después de Formato A...");

        // ✅ CORREGIDO: Pasar stateFactory al método de dominio
        proyecto.manejarFormatoA(stateFactory, "Formato A presentado - " + proyecto.getTitulo());

        logger.info("Estado actualizado a: {}", proyecto.getEstado().getDescripcion());
    }

    @Override
    protected NotificationRequest construirNotificacion(ProyectoGrado proyecto, DocumentData documentData) {
        logger.info("Construyendo notificación para Formato A...");

        String mensaje = String.format(
                "Se ha presentado un nuevo Formato A para el proyecto: %s\n" +
                        "Modalidad: %s\n" +
                        "Docente: %s\n" +
                        "Por favor revise el documento en el sistema.",
                proyecto.getTitulo(),
                documentData.getModalidad(),
                documentData.getUsuarioId()
        );

        return NotificationRequest.builder()
                .notificationType(NotificationType.FORMATO_A_PRESENTADO)
                .subject("Nuevo Formato A Presentado - " + proyecto.getTitulo())
                .message(mensaje)
                .recipients(List.of(
                        Recipient.builder()
                                .email("coordinador@unicauca.edu.co")
                                .role("COORDINATOR")
                                .name("Coordinador")
                                .build()
                ))
                .businessContext(Map.of(
                        "proyectoId", proyecto.getId(),
                        "proyectoTitulo", proyecto.getTitulo(),
                        "docenteId", documentData.getUsuarioId(),
                        "modalidad", documentData.getModalidad(),
                        "intento", proyecto.getIntentosFormatoA()
                ))
                .channel("email")
                .build();
    }

    @Override
    protected void registrarAuditoriaEspecifica(ProyectoGrado proyecto, DocumentData documentData, String documentId) {
        // Auditoría específica para Formato A
        logger.info("Auditoría específica de Formato A - Intento: {}, Modalidad: {}",
                proyecto.getIntentosFormatoA(), documentData.getModalidad());

        // Aquí podrías guardar en una tabla de auditoría específica
    }

    @Override
    protected void preProcesar(ProyectoGrado proyecto, DocumentData documentData) {
        // Pre-procesamiento específico para Formato A
        logger.info("Pre-procesando Formato A - Generando metadatos...");

        // Podrías extraer metadatos, generar resumen, etc.
    }
}