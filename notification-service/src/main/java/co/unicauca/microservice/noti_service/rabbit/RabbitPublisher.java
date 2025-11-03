package co.unicauca.microservice.noti_service.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
@Component
public class RabbitPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishProjectStatusChanged(String projectId, String newState, String messageText) {
        Map<String, Object> message = new HashMap<>();
        message.put("projectId", projectId);
        message.put("newState", newState);
        message.put("message", messageText);
        message.put("timestamp", Instant.now().toString());
        message.put("eventType", "PROJECT_STATUS_CHANGED");

        // Publicar en la cola principal
        rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATIONS_QUEUE, message);
        System.out.println("Published to RabbitMQ [queue: " + RabbitConfig.NOTIFICATIONS_QUEUE + "]: " + message);
    }
}

