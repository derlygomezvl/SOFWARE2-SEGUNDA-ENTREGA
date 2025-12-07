package co.unicauca.comunicacionmicroservicios.port.in.web;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author javiersolanop777
 */
public interface SubmissionWebPort {

    @GetMapping("/health")
    public String health();
}
