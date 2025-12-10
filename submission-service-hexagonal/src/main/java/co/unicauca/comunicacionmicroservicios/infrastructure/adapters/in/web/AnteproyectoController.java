package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.web;

import co.unicauca.comunicacionmicroservicios.application.dto.*;
import co.unicauca.comunicacionmicroservicios.domain.ports.in.web.IAnteproyectoWebPort;
import co.unicauca.comunicacionmicroservicios.infrastructure.util.SecurityRules;
import co.unicauca.comunicacionmicroservicios.domain.services.SubmissionService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Endpoints de Anteproyecto (RF6 + lectura/estado).
 *
 * - POST  /api/submissions/anteproyecto                  (DOCENTE y director del proyecto)
 * - GET   /api/submissions/anteproyecto                  (listado)
 * - PATCH /api/submissions/anteproyecto/{id}/estado      (lo llama Review/Jefe)
 */
@RestController
@RequestMapping("/api/submissions/anteproyecto")
public class AnteproyectoController implements IAnteproyectoWebPort {

    private final SubmissionService service;

    // Constructor explícito en lugar de @RequiredArgsConstructor
    public AnteproyectoController(SubmissionService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<IdResponseDTO> subirAnteproyecto(
        String role,
        String userId,
        AnteproyectoDataDTO data,
        MultipartFile pdf
    )
    {
        SecurityRules.requireDocente(role);
        IdResponseDTO resp = service.subirAnteproyecto(userId, data, pdf);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Override
    public ResponseEntity<AnteproyectoPageDTO> listarAnteproyectos(
        int page,
        int size
    )
    {
        return ResponseEntity.ok(service.listarAnteproyectos(page, size));
    }

    @Override
    public ResponseEntity<Void> cambiarEstadoAnteproyecto(
        String caller,
        Long id,
        CambioEstadoAnteproyectoRequestDTO req
    )
    {
        SecurityRules.requireInternalReviewService(caller);
        service.cambiarEstadoAnteproyecto(id, req);
        return ResponseEntity.ok().build();
    }
}
