package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.clients;

import co.unicauca.comunicacionmicroservicios.domain.ports.out.clients.INotificationClientPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Cliente para comunicación SÍNCRONA con el microservicio de notificaciones
 * Utiliza HTTP/REST para enviar solicitudes directas
 */
@Component
public class NotificationClient implements INotificationClientPort {

    private static final Logger logger = LoggerFactory.getLogger(NotificationClient.class);
    private final WebClient webClient;

    @Autowired
    public NotificationClient(
            @Value("${notification.base-url}") String baseUrl,
            WebClient.Builder builder
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
        logger.info("NotificationClient configurado con baseUrl: {}", baseUrl);
    }

    @Override
    public Mono<Void> sendNotification(Map<String, Object> payload)
    {
        logger.info("Enviando notificación síncrona HTTP...");
        logger.debug("Payload: {}", payload);

        return webClient.post()
                .uri("/send")  // endpoint en el servicio de notificación
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> logger.info("Notificación HTTP enviada correctamente"))
                .onErrorResume(e -> {
                    logger.warn("Error al enviar notificación síncrona (no crítico): {}", e.getMessage());
                    return Mono.empty(); // fallback: no tumbes la petición si notificaciones falla
                });
    }
}
