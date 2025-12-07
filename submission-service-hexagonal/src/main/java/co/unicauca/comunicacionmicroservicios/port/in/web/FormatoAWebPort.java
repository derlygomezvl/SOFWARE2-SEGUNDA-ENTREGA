package co.unicauca.comunicacionmicroservicios.port.in.web;

import co.unicauca.comunicacionmicroservicios.dto.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author javiersolanop777
 */
public interface FormatoAWebPort {

    /**
     * RF2 — Crear Formato A inicial.
     *
     * Recibe:
     *  - data: JSON con los datos del proyecto y formato A (ver FormatoAData)
     *  - pdf:  archivo PDF del Formato A
     *  - carta: PDF de carta (OBLIGATORIA si modalidad = PRACTICA_PROFESIONAL)
     *
     * Reglas:
     *  - Solo DOCENTE puede enviar.
     *  - Crea proyecto de grado y versión v1 del Formato A (intentoActual=1).
     */
    @PostMapping
    public ResponseEntity<IdResponse> crearFormatoA(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId,
            FormatoAData data,
            MultipartFile pdf,
            MultipartFile carta
    );

    /**
     * Obtiene detalles de una versión de Formato A (o vista agregada).
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FormatoAView> obtenerFormatoA(@PathVariable Long id);

    /**
     * Lista Formato A (filtrable por docente).
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FormatoAPage> listarFormatoA(
            @RequestParam(name = "docenteId", required = false) String docenteId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    );

    /**
     * RF4 — Subir nueva versión de Formato A tras un rechazo.
     *
     * Reglas:
     *  - Solo DOCENTE.
     *  - Proyecto debe existir y no estar RECHAZADO_DEFINITIVO.
     *  - Última evaluación debe ser RECHAZADO.
     *  - Máximo 3 intentos.
     */
    @PostMapping(path = "/{proyectoId}/nueva-version")
    public ResponseEntity<IdResponse> nuevaVersion(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long proyectoId,
            MultipartFile pdf,
            MultipartFile carta
    );

    /**
     * Cambiar estado de una versión de Formato A (APROBADO / RECHAZADO).
     * Lo invoca el Review Service (o Coordinador vía Review Service).
     */
    @PatchMapping(path = "/{versionId}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @RequestHeader(value = "X-Service", required = false) String caller,
            @PathVariable Long versionId,
            EvaluacionRequest req
    );


}
