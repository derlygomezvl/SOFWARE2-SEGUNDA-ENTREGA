package co.unicauca.microservice.noti_service.services.notifier;

import co.unicauca.microservice.noti_service.model.Recipient;
import co.unicauca.microservice.noti_service.model.NotificationRequest;

public interface NotificationChannel {
    void send(Recipient recipient, String message, NotificationRequest request, String correlationId, boolean isAsync);
}
