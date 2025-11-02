package co.unicauca.microservice.noti_service.services.notifier;

import co.unicauca.microservice.noti_service.model.Recipient;
import co.unicauca.microservice.noti_service.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationAdapter implements NotificationChannel {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationAdapter.class);

    @Override
    public void send(Recipient recipient, String message, NotificationRequest request, String correlationId, boolean isAsync) {
        String prefix = isAsync ? "ASYNC" : "SYNC";
        log.info("📧 [EMAIL MOCK {}] Enviando correo a: {} ({})", prefix, recipient.email(), recipient.role());
        log.info("   Asunto: {} - {}", request.notificationType(), request.businessContext().get("projectTitle"));
        log.info("   Mensaje:\n{}", message);
        log.info("   CorrelationId: {}", correlationId);
    }
}