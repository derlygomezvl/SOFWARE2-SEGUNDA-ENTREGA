package co.unicauca.review.controller;

import co.unicauca.review.dto.request.AsignacionRequestDTO;
import co.unicauca.review.dto.request.EvaluationRequestDTO;
import co.unicauca.review.dto.response.ApiResponse;
import co.unicauca.review.dto.response.AsignacionDTO;
import co.unicauca.review.dto.response.EvaluationResultDTO;
import co.unicauca.review.enums.AsignacionEstado;
import co.unicauca.review.enums.EvaluatorRole;
import co.unicauca.review.exception.InvalidStateException;
import co.unicauca.review.exception.UnauthorizedException;
import co.unicauca.review.service.AsignacionService;
import co.unicauca.review.service.impl.AnteproyectoEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review/anteproyectos")
@Tag(name = "Anteproyectos", description = "API para gestión y evaluación de anteproyectos")
public class AnteproyectoReviewController {

    private static final Logger log = LoggerFactory.getLogger(AnteproyectoReviewController.class);

    private final AnteproyectoEvaluationService evaluationService;
    private final AsignacionService asignacionService;

    public AnteproyectoReviewController(
            AnteproyectoEvaluationService evaluationService,
            AsignacionService asignacionService) {
        this.evaluationService = evaluationService;
        this.asignacionService = asignacionService;
    }

    @Operation(
            summary = "Listar asignaciones de evaluadores",
            description = "Obtiene una lista paginada de asignaciones de evaluadores. " +
                    "JEFE_DEPARTAMENTO ve todas, EVALUADOR solo ve sus asignaciones."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lista obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Usuario no autorizado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @GetMapping("/asignaciones")
    public ResponseEntity<ApiResponse<Page<AsignacionDTO>>> getAsignaciones(
            @Parameter(description = "ID del usuario", required = true)
            @RequestHeader("X-User-Id") Long userId,

            @Parameter(description = "Rol del usuario", required = true)
            @RequestHeader("X-User-Role") String userRole,

            @Parameter(description = "Estado de la asignación", example = "PENDIENTE")
            @RequestParam(required = false) AsignacionEstado estado,

            @Parameter(description = "Número de página", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        log.info("Listando asignaciones. UserId: {}, Role: {}", userId, userRole);

        try {
            Page<AsignacionDTO> asignaciones;

            if ("JEFE_DEPARTAMENTO".equals(userRole)) {
                asignaciones = asignacionService.findAll(estado, page, size);
            } else if ("EVALUADOR".equals(userRole)) {
                asignaciones = asignacionService.findByEvaluador(userId, estado, page, size);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Rol no autorizado"));
            }

            return ResponseEntity.ok(ApiResponse.success(asignaciones));

        } catch (Exception e) {
            log.error("Error obteniendo asignaciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener asignaciones"));
        }
    }

    @Operation(
            summary = "Asignar evaluadores a anteproyecto",
            description = "Permite al jefe de departamento asignar dos evaluadores a un anteproyecto. " +
                    "Los evaluadores deben ser diferentes."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Evaluadores asignados exitosamente",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (evaluadores iguales, etc.)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Usuario no autorizado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "El anteproyecto ya tiene evaluadores asignados"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping("/asignar")
    public ResponseEntity<ApiResponse<AsignacionDTO>> asignar(
            @Parameter(description = "Datos para la asignación", required = true)
            @Valid @RequestBody AsignacionRequestDTO request,

            @Parameter(description = "ID del usuario", required = true)
            @RequestHeader("X-User-Id") Long userId,

            @Parameter(description = "Rol del usuario", required = true)
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Asignando evaluadores: anteproyecto={}, eval1={}, eval2={}",
                request.anteproyectoId(), request.evaluador1Id(), request.evaluador2Id());

        if (!"JEFE_DEPARTAMENTO".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Solo jefes de departamento pueden asignar evaluadores"));
        }

        try {
            AsignacionDTO asignacion = asignacionService.asignar(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(asignacion, "Evaluadores asignados exitosamente"));

        } catch (IllegalArgumentException e) {
            log.warn("Datos inválidos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("Error asignando evaluadores: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al asignar evaluadores"));
        }
    }

    @Operation(
            summary = "Evaluar anteproyecto",
            description = "Permite a un evaluador asignado evaluar un anteproyecto. " +
                    "Cuando ambos evaluadores han evaluado, se calcula la decisión final."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Evaluación registrada exitosamente",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o estado incorrecto"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Usuario no autorizado o no asignado como evaluador"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @PostMapping("/{id}/evaluar")
    public ResponseEntity<ApiResponse<EvaluationResultDTO>> evaluar(
            @Parameter(description = "ID del anteproyecto a evaluar", required = true)
            @PathVariable Long id,

            @Parameter(description = "Datos de la evaluación", required = true)
            @Valid @RequestBody EvaluationRequestDTO request,

            @Parameter(description = "ID del usuario evaluador", required = true)
            @RequestHeader("X-User-Id") Long userId,

            @Parameter(description = "Rol del usuario", required = true)
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Evaluando anteproyecto: id={}, userId={}", id, userId);

        if (!"EVALUADOR".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Solo evaluadores pueden evaluar anteproyectos"));
        }

        try {
            EvaluationRequestDTO fullRequest = new EvaluationRequestDTO(
                    id,
                    request.decision(),
                    request.observaciones(),
                    userId,
                    EvaluatorRole.EVALUADOR
            );

            EvaluationResultDTO result = evaluationService.evaluate(fullRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(result, "Evaluación registrada exitosamente"));

        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (InvalidStateException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("Error evaluando anteproyecto: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al evaluar anteproyecto"));
        }
    }

    @Operation(
            summary = "Notificar evaluadores asignados",
            description = "Envía notificaciones (simuladas) a los evaluadores asignados. " +
                    "Solo disponible para JEFE_DEPARTAMENTO."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Notificaciones enviadas exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Usuario no autorizado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Asignación no encontrada"
            )
    })
    @PostMapping("/{asignacionId}/notificar")
    public ResponseEntity<ApiResponse<String>> notificarEvaluadores(
            @Parameter(description = "ID de la asignación", required = true)
            @PathVariable Long asignacionId,

            @Parameter(description = "ID del usuario", required = true)
            @RequestHeader("X-User-Id") Long userId,

            @Parameter(description = "Rol del usuario", required = true)
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Enviando notificaciones - Asignación: {}, Usuario: {}", asignacionId, userId);

        if (!"JEFE_DEPARTAMENTO".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Solo jefes de departamento pueden enviar notificaciones"));
        }

        try {
            // Obtener asignación
            AsignacionDTO asignacion = asignacionService.findByAnteproyectoId(asignacionId);

            // Simular notificación
            log.info("📧 NOTIFICACIÓN SIMULADA PARA ASIGNACIÓN {}:", asignacionId);
            log.info("📧 Evaluador 1 (ID: {}): {} - {}",
                    asignacion.evaluador1().id(),
                    asignacion.evaluador1().nombre(),
                    asignacion.evaluador1().email());
            log.info("📧 Evaluador 2 (ID: {}): {} - {}",
                    asignacion.evaluador2().id(),
                    asignacion.evaluador2().nombre(),
                    asignacion.evaluador2().email());
            log.info("📧 Anteproyecto: {}", asignacion.tituloAnteproyecto());
            log.info("📧 Mensaje: Has sido asignado como evaluador de un anteproyecto");

            return ResponseEntity.ok(ApiResponse.success(
                    "Notificaciones enviadas exitosamente (simuladas con logger)"
            ));

        } catch (Exception e) {
            log.error("Error enviando notificaciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al enviar notificaciones"));
        }
    }
}