package co.unicauca.microservice.noti_service.services.notifier;

import org.springframework.stereotype.Service;

@Service
public class ChannelRegistry {
    private final NotificationChannel emailAdapter;

    public ChannelRegistry(EmailNotificationAdapter emailAdapter) {
        this.emailAdapter = emailAdapter;
    }

    public NotificationChannel getChannel(String channelName) {
        return emailAdapter; // Solo usamos email
    }
}