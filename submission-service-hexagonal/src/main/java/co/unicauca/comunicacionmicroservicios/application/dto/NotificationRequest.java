package co.unicauca.comunicacionmicroservicios.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * DTO para el envío de notificaciones asíncronas a través de RabbitMQ.
 * Debe coincidir con la estructura utilizada en el notification-service.
 */
@Builder
public record NotificationRequest(
        @NotNull(message = "Notification type is required")
        NotificationType notificationType,

        @NotBlank(message = "Channel is required")
        String channel,  // "email" | "sms"

        @NotEmpty(message = "At least one recipient is required")
        @Valid
        List<Recipient> recipients,

        /**
         * Contexto del mensaje (variables dinámicas según el tipo de notificación)
         */
        @NotNull(message = "Business context is required")
        Map<String, Object> businessContext,

        String subject,     // ✅ Asunto del mensaje (nuevo campo)
        String message,     // Mensaje personalizado opcional
        String templateId,  // ID de plantilla (opcional)
        boolean forceFail   // Para pruebas o simulaciones
) {
    /**
     * Constructor auxiliar para notificaciones simples.
     */
    public NotificationRequest(
            NotificationType notificationType,
            String channel,
            String recipientEmail,
            Map<String, Object> businessContext
    ) {
        this(
                notificationType,
                channel,
                List.of(new Recipient(recipientEmail, null, null)),
                businessContext,
                null, // subject
                null, // message
                null, // templateId
                false
        );
    }
}
