package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.web;

import co.unicauca.comunicacionmicroservicios.domain.ports.in.web.IPingWebPort;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController implements IPingWebPort {

    @Override
    public String ping()
    {
        return "pong";
    }
}
