package com.unicauca.facade_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de RestTemplate para llamadas externas.
 * @author javiersolanop777
 */
@Configuration
public class RestTemplateConfig {

//    @Bean
//    public RestTemplate atrRestTemplate()
//    {
//        return new RestTemplate();
//    }

    @Bean
    public RestTemplate atrRestTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }
}
