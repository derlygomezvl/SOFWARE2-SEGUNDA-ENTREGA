package co.unicauca.comunicacionmicroservicios.service.template;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.dto.NotificationRequest;
import co.unicauca.comunicacionmicroservicios.dto.NotificationType;
import co.unicauca.comunicacionmicroservicios.dto.Recipient;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;
import org.springframework.stereotype.Component;

/**
 * Implementación concreta para reenvío de Formato A con correcciones
 */
@Component
public class FormatoACorregidoProcessingTemplate extends DocumentProcessingTemplate {

    public FormatoACorregidoProcessingTemplate(NotificationPublisher notificationPublisher) {
        super(notificationPublisher);
    }

    @Override
    protected void validarDocumento(DocumentData documentData) {
        logger.info("Validando Formato A corregido...");

        // Validaciones específicas para formato corregido
        if (documentData.getContenido() == null || documentData.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido corregido no puede estar vacío");
        }

        // Verificar que se hayan abordado las observaciones anteriores
        String observacionesAnteriores = (String) documentData.getMetadata().get("observacionesAnteriores");
        if (observacionesAnteriores != null && !observacionesAnteriores.trim().isEmpty()) {
            // Validar que el contenido mencione que se abordaron las correcciones
            if (!documentData.getContenido().toLowerCase().contains("correcciones") &&
                    !documentData.getContenido().toLowerCase().contains("modificaciones")) {
                logger.warn("El formato corregido no menciona explícitamente las correcciones realizadas");
            }
        }

        logger.info("Formato A corregido validado exitosamente");
    }

    @Override
    protected void validarEstadoProyecto(ProyectoGrado proyecto) {
        logger.info("Validando estado del proyecto para Formato A corregido...");

        // Solo permite reenvío en estados específicos
        if (proyecto.getEstado() != ProjectStateEnum.FORMATO_A_RECHAZADO &&
                proyecto.getEstado() != ProjectStateEnum.FORMATO_A_CORRECCIONES) {
            throw new IllegalStateException(
                    "No se puede reenviar Formato A. Estado actual: " + proyecto.getEstado().getDescripcion()
            );
        }

        // Verificar intentos máximos
        if (proyecto.getIntentosFormatoA() >= 3) {
            throw new IllegalStateException("Límite de intentos excedido. Proyecto cancelado.");
        }

        logger.info("Estado del proyecto validado para Formato A corregido");
    }

    @Override
    protected String guardarDocumento(ProyectoGrado proyecto, DocumentData documentData) {
        logger.info("Guardando Formato A corregido en repositorio...");

        String documentId = "FORMATO_A_CORREGIDO_" + proyecto.getId() + "_V" +
                proyecto.getIntentosFormatoA() + "_" + System.currentTimeMillis();

        logger.info("Formato A corregido guardado con ID: {} para proyecto: {}", documentId, proyecto.getId());

        return documentId;
    }

    @Override
    protected void actualizarEstadoProyecto(ProyectoGrado proyecto) {
        logger.info("Actualizando estado del proyecto después de Formato A corregido...");

        // Incrementar contador de intentos
        proyecto.incrementarIntentos();

        // Volver a estado de evaluación
        proyecto.setEstado(ProjectStateEnum.FORMATO_A_EN_EVALUACION);

        logger.info("Estado actualizado a: {}, Intento: {}",
                proyecto.getEstado().getDescripcion(), proyecto.getIntentosFormatoA());
    }

    @Override
    protected NotificationRequest construirNotificacion(ProyectoGrado proyecto, DocumentData documentData) {
        logger.info("Construyendo notificación para Formato A corregido...");

        String mensaje = String.format(
                "Se ha reenviado el Formato A con correcciones para el proyecto: %s\n" +
                        "Intento: %d\n" +
                        "Docente: %s\n" +
                        "Por favor revise las correcciones en el sistema.",
                proyecto.getTitulo(),
                proyecto.getIntentosFormatoA(),
                documentData.getUsuarioId()
        );

        return NotificationRequest.builder()
                .type(NotificationType.FORMATO_A_REENVIADO)
                .subject("Formato A Corregido Reenviado - " + proyecto.getTitulo())
                .message(mensaje)
                .recipient(Recipient.builder()
                        .userId("coordinador_id")
                        .email("coordinador@unicauca.edu.co")
                        .build())
                .metadata(Map.of(
                        "proyectoId", proyecto.getId(),
                        "proyectoTitulo", proyecto.getTitulo(),
                        "docenteId", documentData.getUsuarioId(),
                        "intento", proyecto.getIntentosFormatoA(),
                        "esReenvio", true
                ))
                .build();
    }

    @Override
    protected void manejarError(ProyectoGrado proyecto, DocumentData documentData, Exception error) {
        // Manejo específico de errores para reenvíos
        logger.error("Error en reenvío de Formato A - Proyecto: {}, Intento: {}, Error: {}",
                proyecto.getId(), proyecto.getIntentosFormatoA(), error.getMessage());

        // Podrías notificar al docente sobre el error en el reenvío
    }
}