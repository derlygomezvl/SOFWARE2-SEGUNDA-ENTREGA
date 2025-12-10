package co.unicauca.comunicacionmicroservicios.domain.ports.out.clients;

import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author javiersolanop777
 */
public interface INotificationClientPort {

    /**
     * Envía una notificación de forma síncrona al servicio de notificaciones
     * @param payload Datos de la notificación (to, subject, body, etc.)
     * @return Mono<Void> para manejo reactivo
     */
    public Mono<Void> sendNotification(Map<String, Object> payload);
}
