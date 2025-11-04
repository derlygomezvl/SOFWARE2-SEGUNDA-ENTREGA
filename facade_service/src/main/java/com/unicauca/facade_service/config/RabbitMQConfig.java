package com.unicauca.facade_service.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

/**
 * @author javiersolanop777
 */
@Configuration
public class RabbitMQConfig {

    public static final String ATR_FACADE_QUEUE = "facadeQueue";

    @Bean
    public Queue facadeQueue()
    {
        return new Queue(ATR_FACADE_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter()
    {
        return new Jackson2JsonMessageConverter();
    }
}
