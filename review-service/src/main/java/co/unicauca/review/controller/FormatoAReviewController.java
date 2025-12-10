package co.unicauca.review.controller;

import co.unicauca.review.client.SubmissionServiceClient;
import co.unicauca.review.dto.request.EvaluationRequestDTO;
import co.unicauca.review.dto.response.ApiResponse;
import co.unicauca.review.dto.response.EvaluationResultDTO;
import co.unicauca.review.dto.response.FormatoAReviewDTO;
import co.unicauca.review.enums.EvaluatorRole;
import co.unicauca.review.exception.InvalidStateException;
import co.unicauca.review.exception.UnauthorizedException;
import co.unicauca.review.service.impl.FormatoAEvaluationService;
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
@RequestMapping("/api/review/formatoA")
@Tag(name = "Formato A", description = "API para evaluación de Formatos A")
public class FormatoAReviewController {

    private static final Logger log = LoggerFactory.getLogger(FormatoAReviewController.class);

    private final FormatoAEvaluationService evaluationService;
    private final SubmissionServiceClient submissionClient;

    public FormatoAReviewController(
            FormatoAEvaluationService evaluationService,
            SubmissionServiceClient submissionClient) {
        this.evaluationService = evaluationService;
        this.submissionClient = submissionClient;
    }

    @Operation(
            summary = "Listar Formatos A pendientes",
            description = "Obtiene una lista paginada de Formatos A pendientes de evaluación. Solo para COORDINADOR."
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
    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<Page<FormatoAReviewDTO>>> getPendientes(
            @Parameter(description = "Rol del usuario", required = true)
            @RequestHeader("X-User-Role") String userRole,

            @Parameter(description = "Número de página", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        log.info("Listando Formato A pendientes. UserRole: {}", userRole);

        if (!"COORDINADOR".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Rol no autorizado"));
        }

        try {
            Page<FormatoAReviewDTO> formatosA =
                    submissionClient.getFormatosAPendientes(page, size);

            return ResponseEntity.ok(ApiResponse.success(formatosA));

        } catch (Exception e) {
            log.error("Error obteniendo Formato A pendientes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener Formato A pendientes"));
        }
    }

    @Operation(
            summary = "Evaluar Formato A",
            description = "Permite a un coordinador evaluar un Formato A. Se registra la decisión y observaciones."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Formato A evaluado exitosamente",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o estado incorrecto"
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
    @PostMapping("/{id}/evaluar")
    public ResponseEntity<ApiResponse<EvaluationResultDTO>> evaluar(
            @Parameter(description = "ID del Formato A a evaluar", required = true)
            @PathVariable Long id,

            @Parameter(description = "Datos de la evaluación", required = true)
            @Valid @RequestBody EvaluationRequestDTO request,

            @Parameter(description = "ID del usuario evaluador", required = true)
            @RequestHeader("X-User-Id") Long userId,

            @Parameter(description = "Rol del usuario", required = true)
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Evaluando Formato A: id={}, userId={}, role={}", id, userId, userRole);

        if (!"COORDINADOR".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Solo coordinadores pueden evaluar Formato A"));
        }

        try {
            EvaluationRequestDTO fullRequest = new EvaluationRequestDTO(
                    id,
                    request.decision(),
                    request.observaciones(),
                    userId,
                    EvaluatorRole.COORDINADOR
            );

            EvaluationResultDTO result = evaluationService.evaluate(fullRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(result, "Formato A evaluado exitosamente"));

        } catch (UnauthorizedException e) {
            log.warn("Acceso no autorizado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (InvalidStateException e) {
            log.warn("Estado inválido: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("Error evaluando Formato A: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al evaluar Formato A"));
        }
    }
}