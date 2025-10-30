/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.comunicacionmicroservicios.controller;

/**
 *
 * @author USUARIO
 */

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.unicauca.comunicacionmicroservicios.dto.*;
import co.unicauca.comunicacionmicroservicios.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor

public class SubmissionController {
  private final SubmissionService service;

    @GetMapping("/health")
    public String health() { return "ok"; }
}


