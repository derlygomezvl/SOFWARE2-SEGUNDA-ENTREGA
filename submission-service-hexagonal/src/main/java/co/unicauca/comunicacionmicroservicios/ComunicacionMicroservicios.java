package co.unicauca.comunicacionmicroservicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = "co.unicauca.comunicacionmicroservicios"
)
@EnableJpaRepositories(
    basePackages = "co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.db.repository"
)
@EntityScan(
    basePackages = "co.unicauca.comunicacionmicroservicios.domain.model"
)
public class ComunicacionMicroservicios {
    
    public static void main(String[] args) 
    {
        SpringApplication.run(ComunicacionMicroservicios.class, args);
    }
}
