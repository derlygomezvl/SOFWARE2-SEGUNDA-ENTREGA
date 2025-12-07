package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.web;

import co.unicauca.comunicacionmicroservicios.application.dto.*;
import co.unicauca.comunicacionmicroservicios.domain.ports.in.web.FormatoAWebPort;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.Submission;
import co.unicauca.comunicacionmicroservicios.infrastructure.util.SecurityRules;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Endpoints de Formato A (RF2, RF4 y lecturas).
 *
 * - POST  /api/submissions/formatoA                       (DOCENTE)
 * - GET   /api/submissions/formatoA/{id}
 * - GET   /api/submissions/formatoA?docenteId=...&page=&size=
 * - POST  /api/submissions/formatoA/{proyectoId}/nueva-version (DOCENTE)
 * - PATCH /api/submissions/formatoA/{versionId}/estado   (lo llama Review Service)
 */
@RestController
@RequestMapping("/api/submissions/formatoA")
public class FormatoAController implements FormatoAWebPort {

    private final Submission service;

    // Constructor explícito en lugar de @RequiredArgsConstructor
    public FormatoAController(Submission service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<IdResponse> crearFormatoA(
        String role,
        String userId,
        FormatoAData data,
        MultipartFile pdf,
        MultipartFile carta
    )
    {
        SecurityRules.requireDocente(role);
        IdResponse resp = service.crearFormatoA(userId, data, pdf, carta);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Override
    public ResponseEntity<FormatoAView> obtenerFormatoA(Long id)
    {
        return ResponseEntity.ok(service.obtenerFormatoA(id));
    }

    @Override
    public ResponseEntity<FormatoAPage> listarFormatoA(
        String docenteId,
        int page,
        int size
    )
    {
        return ResponseEntity.ok(service.listarFormatoA(Optional.ofNullable(docenteId), page, size));
    }

    @Override
    public ResponseEntity<IdResponse> nuevaVersion(
        String role,
        String userId,
        Long proyectoId,
        MultipartFile pdf,
        MultipartFile carta
    )
    {
        SecurityRules.requireDocente(role);
        IdResponse resp = service.reenviarFormatoA(userId, proyectoId, pdf, carta);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Override
    public ResponseEntity<Void> cambiarEstado(
        String caller,
        Long versionId,
        EvaluacionRequest req
    )
    {
        SecurityRules.requireInternalReviewService(caller);
        service.cambiarEstadoFormatoA(versionId, req);
        return ResponseEntity.ok().build();
    }
}
