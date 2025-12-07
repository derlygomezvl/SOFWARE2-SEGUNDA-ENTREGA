package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.events;

import co.unicauca.comunicacionmicroservicios.domain.ports.out.events.INotificationPublisherPort;
import co.unicauca.comunicacionmicroservicios.infrastructure.config.RabbitConfig;
import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.application.dto.NotificationRequest;
import co.unicauca.comunicacionmicroservicios.application.dto.NotificationType;
import co.unicauca.comunicacionmicroservicios.application.dto.Recipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para enviar notificaciones asíncronas a través de RabbitMQ.
 */
@Component
public class NotificationPublisher implements INotificationPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notificarAprobacionFormatoA(ProyectoGrado proyecto)
    {
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

    @Override
    public void notificarRechazoFormatoA(ProyectoGrado proyecto, String observaciones)
    {
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

    @Override
    public void notificarCorreccionesFormatoA(ProyectoGrado proyecto, String observaciones)
    {
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

    @Override
    public void notificarReenvioFormatoA(ProyectoGrado proyecto)
    {
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

    @Override
    public void notificarRechazoDefinitivoFormatoA(ProyectoGrado proyecto)
    {
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

    @Override
    public void notificarAnteproyectoPresentado(ProyectoGrado proyecto)
    {
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

    @Override
    public void notificarAprobacionAnteproyecto(ProyectoGrado proyecto)
    {
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

    @Override
    public void notificarRechazoAnteproyecto(ProyectoGrado proyecto, String observaciones)
    {
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

    @Override
    public void notificarReenvioAnteproyecto(ProyectoGrado proyecto)
    {
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

    @Override
    public void notificarAsignacionEvaluadores(ProyectoGrado proyecto)
    {
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

    @Override
    public void publishNotification(NotificationRequest request, String eventDescription)
    {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;

        try {
            log.info("Intentando enviar notificación: {} - CorrelationId: {}", eventDescription, finalCorrelationId);

            MessagePostProcessor processor = message -> {
                message.getMessageProperties().setHeader("X-Correlation-Id", finalCorrelationId);
                return message;
            };

            rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATIONS_QUEUE, request, processor);
            log.info("Notificación publicada exitosamente: {} - CorrelationId: {}", eventDescription, finalCorrelationId);

        } catch (Exception e) {

            log.warn("RabbitMQ no disponible, pero el proceso continuó exitosamente. Error: {}", e.getMessage());


            String recipientEmails = request.recipients().stream()
                    .map(recipient -> recipient.email())
                    .collect(Collectors.joining(", "));

            String messagePreview = request.message() != null ?
                    request.message().substring(0, Math.min(50, request.message().length())) + "..." :
                    "Sin mensaje";

            log.info("Notificación simulada - Tipo: {}, Para: {}, Asunto: {}, Mensaje: {}",
                    eventDescription,
                    recipientEmails,
                    request.subject() != null ? request.subject() : "Sin asunto",
                    messagePreview);
        }
    }

    @Override
    public void notificarFormatoAPresentado(ProyectoGrado proyecto, String contenido)
    {
        try {
            NotificationRequest notificacion = NotificationRequest.builder()
                    .notificationType(NotificationType.FORMATO_A_PRESENTADO)
                    .subject("Nuevo Formato A Presentado")
                    .message("Se ha presentado un nuevo Formato A para el proyecto: " + proyecto.getTitulo() + "\nContenido: " + contenido)
                    .recipients(List.of(
                            Recipient.builder()
                                    .email("coordinador@unicauca.edu.co")
                                    .role("COORDINATOR")
                                    .build()
                    ))
                    .businessContext(Map.of("projectId", proyecto.getId()))
                    .channel("email")
                    .build();

            publishNotification(notificacion, "Formato A presentado desde State Pattern");
        } catch (Exception e) {
            log.warn("Error en notificación Formato A presentado, pero proceso continuó: {}", e.getMessage());
        }
    }
}