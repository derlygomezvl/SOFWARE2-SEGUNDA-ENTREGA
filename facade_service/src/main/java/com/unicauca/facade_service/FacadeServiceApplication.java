package com.unicauca.facade_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Facade service application entrypoint.
 * @author javiersolanop777
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.unicauca.facade_service.config",
        "com.unicauca.facade_service.controller",
        "com.unicauca.facade_service.dto",
        "com.unicauca.facade_service.facade",
        "com.unicauca.facade_service.service"
})
@EnableFeignClients(basePackages = "com.unicauca.facade_service")
public class FacadeServiceApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(FacadeServiceApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}
