package co.unicauca.comunicacionmicroservicios.domain.ports.in.web;

import co.unicauca.comunicacionmicroservicios.application.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author javiersolanop777
 */
public interface IAnteproyectoWebPort {

    /**
     * RF6 — Subir anteproyecto.
     *
     * Reglas:
     *  - Solo DOCENTE.
     *  - Debe ser el director del proyecto.
     *  - Formato A debe estar APROBADO.
     *  - No debe existir anteproyecto previo para el proyecto.
     */
    @PostMapping
    public ResponseEntity<IdResponseDTO> subirAnteproyecto(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId,
            AnteproyectoDataDTO data,
            MultipartFile pdf
    );

    @GetMapping
    public ResponseEntity<AnteproyectoPageDTO> listarAnteproyectos(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    );

    /**
     * Cambio de estado de un anteproyecto (invocado por Review/Jefe).
     */
    @PatchMapping(path = "/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoAnteproyecto(
            @RequestHeader(value = "X-Service", required = false) String caller,
            @PathVariable Long id,
            CambioEstadoAnteproyectoRequestDTO req
    );
    /**
     * NUEVO: Obtener un anteproyecto por ID (necesario para Review Service)
     */
    @GetMapping(path = "/{id}") // 👈 Endpoint faltante
    public ResponseEntity<AnteproyectoViewDTO> obtenerAnteproyecto(@PathVariable Long id);


}
