package co.unicauca.review.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Inyecta las propiedades desde application.yml
    @Value("${evaluation.exchange}")
    private String exchangeName;

    @Value("${evaluation.queue}")
    private String queueName;

    @Value("${evaluation.routing-key}")
    private String routingKey;

    /**
     * Declara el Exchange (punto de entrada de mensajes)
     * Usamos DirectExchange porque la clave de ruteo será exacta.
     */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    /**
     * Declara la Queue (cola de notificaciones)
     * durable = true: Persiste aunque RabbitMQ se reinicie.
     */
    @Bean
    public Queue notificationQueue() {
        return new Queue(queueName, true);
    }

    /**
     * Vincula la Queue al Exchange usando la Routing Key.
     */
    @Bean
    public Binding binding(Queue notificationQueue, DirectExchange exchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(exchange)
                .with(routingKey);
    }
}