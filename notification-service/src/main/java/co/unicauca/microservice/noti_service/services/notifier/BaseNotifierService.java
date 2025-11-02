package co.unicauca.microservice.noti_service.services.notifier;

import co.unicauca.microservice.noti_service.model.NotificationRequest;
import co.unicauca.microservice.noti_service.model.NotificationResponse;
import co.unicauca.microservice.noti_service.model.Recipient;
import co.unicauca.microservice.noti_service.rabbit.RabbitConfig;
import co.unicauca.microservice.noti_service.services.template.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación base del servicio de notificaciones.
 * Responsabilidad única: enviar notificaciones usando plantillas.
 * Los decorators agregarán logging, validación, persistencia, etc.
 */
@Service("baseNotifierService")
public class BaseNotifierService implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(BaseNotifierService.class);

    private final RabbitTemplate rabbitTemplate;
    private final TemplateService templateService;
    private final ChannelRegistry channelRegistry;

    public BaseNotifierService(RabbitTemplate rabbitTemplate,
                               TemplateService templateService,
                               ChannelRegistry channelRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.templateService = templateService;
        this.channelRegistry = channelRegistry;
    }

    @Override
    public NotificationResponse sendSync(NotificationRequest request) {
        String correlationId = MDC.get("correlationId");

        if (request.forceFail()) {
            throw new RuntimeException("Forced failure in synchronous send");
        }

        // Resolver mensaje usando plantilla
        String message = resolveMessage(request);

        // Enviar a cada destinatario
        List<String> failedRecipients = new ArrayList<>();
        for (Recipient recipient : request.recipients()) {
            try {
                sendToRecipient(recipient, message, request, correlationId, false);
            } catch (Exception e) {
                log.error("Failed to send to recipient: {}", recipient.email(), e);
                failedRecipients.add(recipient.email());
            }
        }

        // Determinar estado
        String status = determineStatus(request.recipients().size(), failedRecipients.size());

        return new NotificationResponse(
                UUID.randomUUID(),
                request.notificationType(),
                status,
                correlationId,
                request.recipients().size(),
                failedRecipients,
                LocalDateTime.now()
        );
    }

    @Override
    public void publishAsync(NotificationRequest request, String correlationId) {
        MessagePostProcessor processor = msg -> {
            msg.getMessageProperties().setHeader(RabbitConfig.HEADER_CORRELATION, correlationId);
            return msg;
        };

        rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATIONS_QUEUE, request, processor);
    }

    @Override
    public void send(NotificationRequest request, String correlationId) {
        if (request.forceFail()) {
            throw new RuntimeException("Forced failure for async processing");
        }

        // Resolver mensaje usando plantilla
        String message = resolveMessage(request);

        // Enviar a cada destinatario
        for (Recipient recipient : request.recipients()) {
            sendToRecipient(recipient, message, request, correlationId, true);
        }
    }

    /**
     * Resuelve el mensaje usando plantillas o mensaje directo
     */
    private String resolveMessage(NotificationRequest request) {
        // Si hay mensaje custom, usarlo
        if (request.message() != null && !request.message().isBlank()) {
            return request.message();
        }

        // Determinar ID de plantilla
        String templateId = request.templateId();
        if (templateId == null || templateId.isBlank()) {
            templateId = templateService.getDefaultTemplateId(request.notificationType());
        }

        // Resolver plantilla con contexto
        return templateService.resolveTemplate(templateId, request.businessContext());
    }

    /**
     * Envía notificación a un destinatario específico
     */
    private void sendToRecipient(Recipient recipient, String message,
                                 NotificationRequest request,
                                 String correlationId,
                                 boolean isAsync) {
        NotificationChannel channel = channelRegistry.getChannel(request.channel());
        channel.send(recipient, message, request, correlationId, isAsync);
    }

    /**
     * Determina el estado final basado en éxitos y fallos
     */
    private String determineStatus(int total, int failed) {
        if (failed == 0) {
            return "SENT";
        } else if (failed < total) {
            return "PARTIALLY_SENT";
        } else {
            return "FAILED";
        }
    }
}