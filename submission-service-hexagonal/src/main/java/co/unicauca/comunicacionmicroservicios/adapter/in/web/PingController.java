package co.unicauca.comunicacionmicroservicios.adapter.in.web;

import co.unicauca.comunicacionmicroservicios.port.in.web.PingWebPort;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController implements PingWebPort {

    @Override
    public String ping()
    {
        return "pong";
    }
}
