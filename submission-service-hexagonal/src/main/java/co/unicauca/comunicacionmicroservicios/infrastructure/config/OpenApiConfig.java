package co.unicauca.comunicacionmicroservicios.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Submission Service API")
                        .version("1.0.0")
                        .description("API para gestión de documentos de trabajo de grado con patrones State y Template Method")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@unicauca.edu.co")));
    }
}
