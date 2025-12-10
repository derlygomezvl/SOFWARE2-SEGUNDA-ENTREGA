package co.unicauca.comunicacionmicroservicios.domain.ports.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author javiersolanop777
 */
public interface ISubmissionWebPort {

    @GetMapping("/health")
    @PreAuthorize("hasRole('DOCENTE')")
    public String health();
}
