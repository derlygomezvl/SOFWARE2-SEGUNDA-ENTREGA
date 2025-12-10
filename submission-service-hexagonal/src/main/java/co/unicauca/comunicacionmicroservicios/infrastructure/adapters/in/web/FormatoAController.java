package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.web;

import co.unicauca.comunicacionmicroservicios.application.dto.*;
import co.unicauca.comunicacionmicroservicios.domain.ports.in.web.IFormatoAWebPort;
import co.unicauca.comunicacionmicroservicios.domain.services.SubmissionService;
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
public class FormatoAController implements IFormatoAWebPort {

    private final SubmissionService service;

    // Constructor explícito en lugar de @RequiredArgsConstructor
    public FormatoAController(SubmissionService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<IdResponseDTO> crearFormatoA(
        String role,
        String userId,
        FormatoADataDTO data,
        MultipartFile pdf,
        MultipartFile carta
    )
    {
//        SecurityRules.requireDocente(role);
        IdResponseDTO resp = service.crearFormatoA(userId, data, pdf, carta);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Override
    public ResponseEntity<FormatoAViewDTO> obtenerFormatoA(Long id)
    {
        return ResponseEntity.ok(service.obtenerFormatoA(id));
    }

    @Override
    public ResponseEntity<FormatoAPageDTO> listarFormatoA(
        String docenteId,
        int page,
        int size
    )
    {
        return ResponseEntity.ok(service.listarFormatoA(Optional.ofNullable(docenteId), page, size));
    }

    @Override
    public ResponseEntity<IdResponseDTO> nuevaVersion(
        String role,
        String userId,
        Long proyectoId,
        MultipartFile pdf,
        MultipartFile carta
    )
    {
//        SecurityRules.requireDocente(role);
        IdResponseDTO resp = service.reenviarFormatoA(userId, proyectoId, pdf, carta);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Override
    public ResponseEntity<Void> cambiarEstado(
        String caller,
        Long versionId,
        EvaluacionRequestDTO req
    )
    {
//        SecurityRules.requireInternalReviewService(caller);
        service.cambiarEstadoFormatoA(versionId, req);
        return ResponseEntity.ok().build();
    }
}
