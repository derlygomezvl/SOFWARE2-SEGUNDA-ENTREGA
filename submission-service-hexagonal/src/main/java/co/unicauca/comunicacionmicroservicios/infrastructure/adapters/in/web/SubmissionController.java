/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.web;

/**
 * Controlador básico de salud (health check).
 * Los endpoints principales están en FormatoAController y AnteproyectoController.
 */

import co.unicauca.comunicacionmicroservicios.domain.ports.in.web.SubmissionWebPort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController implements SubmissionWebPort {

    @Override
    public String health()
    {
        return "Submission Service OK hexagonal";
    }
}
