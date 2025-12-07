package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.web;

import co.unicauca.comunicacionmicroservicios.domain.ports.in.web.PingWebPort;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController implements PingWebPort {

    @Override
    public String ping()
    {
        return "pong";
    }
}
