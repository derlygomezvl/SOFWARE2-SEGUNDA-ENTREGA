package co.unicauca.comunicacionmicroservicios.adapter.in.web;

import co.unicauca.comunicacionmicroservicios.dto.*;
import co.unicauca.comunicacionmicroservicios.port.in.web.AnteproyectoWebPort;
import co.unicauca.comunicacionmicroservicios.util.SecurityRules;
import co.unicauca.comunicacionmicroservicios.service.SubmissionService;
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
public class AnteproyectoController implements AnteproyectoWebPort {

    private final SubmissionService service;

    // Constructor explícito en lugar de @RequiredArgsConstructor
    public AnteproyectoController(SubmissionService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<IdResponse> subirAnteproyecto(
        String role,
        String userId,
        AnteproyectoData data,
        MultipartFile pdf
    )
    {
        SecurityRules.requireDocente(role);
        IdResponse resp = service.subirAnteproyecto(userId, data, pdf);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Override
    public ResponseEntity<AnteproyectoPage> listarAnteproyectos(
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
        CambioEstadoAnteproyectoRequest req
    )
    {
        SecurityRules.requireInternalReviewService(caller);
        service.cambiarEstadoAnteproyecto(id, req);
        return ResponseEntity.ok().build();
    }
}
