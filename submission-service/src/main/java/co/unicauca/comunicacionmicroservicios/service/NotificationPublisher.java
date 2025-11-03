package co.unicauca.comunicacionmicroservicios.service;

import co.unicauca.comunicacionmicroservicios.config.RabbitConfig;
import co.unicauca.comunicacionmicroservicios.dto.NotificationRequest;
import co.unicauca.comunicacionmicroservicios.dto.NotificationType;
import co.unicauca.comunicacionmicroservicios.dto.Recipient;
import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio para enviar notificaciones asíncronas a través de RabbitMQ.
 */
@Service
public class NotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // ================== MÉTODOS DE NOTIFICACIÓN ==================

    public void notificarAprobacionFormatoA(ProyectoGrado proyecto) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.FORMATO_A_APROBADO)
                .subject("Formato A Aprobado")
                .message("Su Formato A para el proyecto '" + proyecto.getTitulo() + "' ha sido aprobado. Ya puede subir el anteproyecto.")
                .recipients(List.of(
                        Recipient.builder()
                                .email("docente@unicauca.edu.co")
                                .role("TEACHER")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Formato A aprobado");
    }

    public void notificarRechazoFormatoA(ProyectoGrado proyecto, String observaciones) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.FORMATO_A_RECHAZADO)
                .subject("Formato A Requiere Correcciones")
                .message("Su Formato A para el proyecto '" + proyecto.getTitulo() + "' ha sido rechazado. Observaciones: " + observaciones)
                .recipients(List.of(
                        Recipient.builder()
                                .email("docente@unicauca.edu.co")
                                .role("TEACHER")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Formato A rechazado");
    }

    public void notificarCorreccionesFormatoA(ProyectoGrado proyecto, String observaciones) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.FORMATO_A_CORRECCIONES)
                .subject("Formato A Requiere Correcciones")
                .message("Su Formato A para el proyecto '" + proyecto.getTitulo() + "' requiere correcciones. Observaciones: " + observaciones)
                .recipients(List.of(
                        Recipient.builder()
                                .email("docente@unicauca.edu.co")
                                .role("TEACHER")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Correcciones Formato A");
    }

    public void notificarReenvioFormatoA(ProyectoGrado proyecto) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.FORMATO_A_REENVIADO)
                .subject("Formato A Reenviado")
                .message("El Formato A para el proyecto '" + proyecto.getTitulo() + "' ha sido reenviado para evaluación.")
                .recipients(List.of(
                        Recipient.builder()
                                .email("coordinador@unicauca.edu.co")
                                .role("COORDINATOR")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Formato A reenviado");
    }

    public void notificarRechazoDefinitivoFormatoA(ProyectoGrado proyecto) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.FORMATO_A_RECHAZADO)
                .subject("Rechazo Definitivo de Formato A")
                .message("El Formato A del proyecto '" + proyecto.getTitulo() + "' ha sido rechazado definitivamente. "
                        + "No se permiten más reenvíos debido a que alcanzó el número máximo de intentos.")
                .recipients(List.of(
                        Recipient.builder()
                                .email("coordinador@unicauca.edu.co")
                                .role("COORDINATOR")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Rechazo definitivo Formato A");
    }

    public void notificarAnteproyectoPresentado(ProyectoGrado proyecto) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.ANTEPROYECTO_PRESENTADO)
                .subject("Nuevo Anteproyecto Presentado")
                .message("Se ha presentado un nuevo anteproyecto para el proyecto: " + proyecto.getTitulo())
                .recipients(List.of(
                        Recipient.builder()
                                .email("jefe.departamento@unicauca.edu.co")
                                .role("DEPARTMENT_HEAD")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Anteproyecto presentado");
    }

    public void notificarAprobacionAnteproyecto(ProyectoGrado proyecto) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.ANTEPROYECTO_APROBADO)
                .subject("Anteproyecto Aprobado")
                .message("Su anteproyecto para el proyecto '" + proyecto.getTitulo() + "' ha sido aprobado. Puede continuar con el desarrollo.")
                .recipients(List.of(
                        Recipient.builder()
                                .email("docente@unicauca.edu.co")
                                .role("TEACHER")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Anteproyecto aprobado");
    }

    public void notificarRechazoAnteproyecto(ProyectoGrado proyecto, String observaciones) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.ANTEPROYECTO_RECHAZADO)
                .subject("Anteproyecto Rechazado")
                .message("Su anteproyecto para el proyecto '" + proyecto.getTitulo() + "' ha sido rechazado. Observaciones: " + observaciones)
                .recipients(List.of(
                        Recipient.builder()
                                .email("docente@unicauca.edu.co")
                                .role("TEACHER")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Anteproyecto rechazado");
    }

    public void notificarReenvioAnteproyecto(ProyectoGrado proyecto) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.ANTEPROYECTO_REENVIADO)
                .subject("Anteproyecto Reenviado")
                .message("El anteproyecto para el proyecto '" + proyecto.getTitulo() + "' ha sido reenviado para evaluación.")
                .recipients(List.of(
                        Recipient.builder()
                                .email("jefe.departamento@unicauca.edu.co")
                                .role("DEPARTMENT_HEAD")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Anteproyecto reenviado");
    }

    public void notificarAsignacionEvaluadores(ProyectoGrado proyecto) {
        NotificationRequest notificacion = NotificationRequest.builder()
                .notificationType(NotificationType.EVALUADORES_ASIGNADOS)
                .subject("Evaluadores Asignados")
                .message("Se han asignado evaluadores para el anteproyecto del proyecto: " + proyecto.getTitulo())
                .recipients(List.of(
                        Recipient.builder()
                                .email("docente@unicauca.edu.co")
                                .role("TEACHER")
                                .build()
                ))
                .businessContext(Map.of("projectId", proyecto.getId()))
                .channel("email")
                .build();

        publishNotification(notificacion, "Evaluadores asignados");
    }




    // ================== MÉTODO BASE ==================

    public void publishNotification(NotificationRequest request, String eventDescription) {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;
        MessagePostProcessor processor = message -> {
            message.getMessageProperties().setHeader("X-Correlation-Id", finalCorrelationId);
            return message;
        };

        try {
            rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATIONS_QUEUE, request, processor);
            log.info("✅ Notificación publicada: {} - CorrelationId: {}", eventDescription, finalCorrelationId);
        } catch (AmqpException e) {
            log.error("❌ Error al enviar notificación: {} - CorrelationId: {}", eventDescription, finalCorrelationId, e);
            throw e;
        }
    }
}
