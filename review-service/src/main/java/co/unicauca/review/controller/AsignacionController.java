package co.unicauca.review.controller;

import co.unicauca.review.dto.request.AsignacionRequestDTO;
import co.unicauca.review.dto.request.DecisionRequestDTO;
import co.unicauca.review.dto.response.ApiResponse;
import co.unicauca.review.dto.response.AsignacionDTO;
import co.unicauca.review.enums.AsignacionEstado;
import co.unicauca.review.enums.EvaluatorRole;
import co.unicauca.review.service.AsignacionService;
import co.unicauca.review.util.SecurityUtil;
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
@RequestMapping("/review/asignaciones")
@Tag(name = "Asignación de Evaluadores", description = "API para asignar evaluadores a anteproyectos")
public class AsignacionController {

    private static final Logger log = LoggerFactory.getLogger(AsignacionController.class);

    private final AsignacionService asignacionService;
    private final SecurityUtil securityUtil;

    public AsignacionController(AsignacionService asignacionService, SecurityUtil securityUtil) {
        this.asignacionService = asignacionService;
        this.securityUtil = securityUtil;
    }

    @Operation(
            summary = "Asignar evaluadores a anteproyecto",
            description = "Asigna dos evaluadores a un anteproyecto. Solo debe ser llamado por un Jefe de Departamento."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Evaluadores asignados exitosamente.",
                    content = @Content(schema = @Schema(implementation = AsignacionDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida o anteproyecto ya asignado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado (El usuario no es JEFE_DEPARTAMENTO)")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<AsignacionDTO>> asignarEvaluadores(
            // **CORRECCIÓN 1: Se añade el header X-User-Role para validar con SecurityUtil**
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody AsignacionRequestDTO request) {

        log.info("Intentando asignar evaluadores al anteproyecto: {}", request.anteproyectoId());

        // 1. Verificar autorización (requisito del sistema)
        // **CORRECCIÓN 1: Se usa validateRole en lugar de checkRole**
        securityUtil.validateRole(userRole, EvaluatorRole.JEFE_DEPARTAMENTO);

        // 2. Ejecutar la lógica de asignación y notificación
        AsignacionDTO asignacion = asignacionService.asignar(request);

        // 3. Respuesta HTTP 201 Created
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        // **CORRECCIÓN 2: Se invierte el orden de los argumentos**
                        asignacion, // Primero el objeto de datos (T)
                        "Evaluadores asignados y evento de notificación enviado a RabbitMQ." // Luego el mensaje (String)
                ));
    }

    // ... (El resto de métodos quedan sin cambios)

    @Operation(
            summary = "Listar asignaciones",
            description = "Obtiene una lista paginada de asignaciones. Puede filtrarse por estado."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AsignacionDTO>>> listarAsignaciones(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @Parameter(description = "Estado de la asignación", example = "PENDIENTE")
            @RequestParam(required = false) AsignacionEstado estado,
            @Parameter(description = "Número de página", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        log.debug("Listando asignaciones - Usuario: {}, Estado: {}, Page: {}, Size: {}",
                userId, estado, page, size);

        // Validar permisos según el rol
        Page<AsignacionDTO> asignaciones;

        try {
            EvaluatorRole currentRole = EvaluatorRole.valueOf(userRole);

            switch (currentRole) {
                case EVALUADOR:
                    // Evaluador solo ve sus asignaciones
                    asignaciones = asignacionService.findByEvaluador(userId, estado, page, size);
                    break;

                case JEFE_DEPARTAMENTO:
                    // Jefe ve todas las asignaciones
                    asignaciones = asignacionService.findAll(estado, page, size);
                    break;

                case COORDINADOR:
                    // Coordinador puede ver asignaciones (opcional)
                    asignaciones = asignacionService.findAll(estado, page, size);
                    break;

                default:
                    throw new SecurityException("Rol no autorizado para ver asignaciones");
            }
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Rol inválido: " + userRole);
        }

        return ResponseEntity.ok(ApiResponse.success(asignaciones));
    }

    @Operation(
            summary = "Obtener asignación por anteproyecto",
            description = "Obtiene la asignación específica de un anteproyecto."
    )
    @GetMapping("/anteproyecto/{anteproyectoId}")
    public ResponseEntity<ApiResponse<AsignacionDTO>> obtenerPorAnteproyecto(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long anteproyectoId) {

        log.debug("Obteniendo asignación del anteproyecto: {}", anteproyectoId);

        // Validar acceso básico
        securityUtil.validateAnyRole(userRole,
                EvaluatorRole.JEFE_DEPARTAMENTO,
                EvaluatorRole.EVALUADOR,
                EvaluatorRole.COORDINADOR);

        AsignacionDTO asignacion = asignacionService.findByAnteproyectoId(anteproyectoId);

        return ResponseEntity.ok(ApiResponse.success(asignacion));
    }

    @Operation(
            summary = "Registrar decisión del evaluador",
            description = "Permite a un evaluador registrar su decisión (APROBADO/RECHAZADO) sobre un anteproyecto asignado."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Decisión registrada exitosamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado o usuario no autorizado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asignación no encontrada.")
    })
    @PatchMapping("/{asignacionId}/evaluador/{evaluadorId}") // <--- ENDPOINT FALTANTE
    public ResponseEntity<ApiResponse<String>> registrarDecision(
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long asignacionId,
            @PathVariable Long evaluadorId,
            @Valid @RequestBody DecisionRequestDTO request) { // Usando el DTO simple

        log.info("Registrando decisión para asignación {} por evaluador {}", asignacionId, evaluadorId);

        // 1. Autorización: Solo el rol EVALUADOR puede llamar a este endpoint
        securityUtil.validateRole(userRole, EvaluatorRole.EVALUADOR);

        // 2. Autorización: El ID del usuario que llama debe coincidir con el ID del evaluador en la URL
        if (!userId.equals(evaluadorId)) {
            throw new SecurityException("No tiene permiso para registrar decisiones para otro evaluador.");
        }

        // 3. Ejecutar la lógica de negocio
        asignacionService.registrarDecision(
                asignacionId, evaluadorId, request.decision(), request.observaciones());

        String message = String.format("Decisión registrada (%s).", request.decision());

        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @Operation(
            summary = "Notificar evaluadores asignados",
            description = "Envía notificaciones a los evaluadores asignados (simulado con logger)."
    )
    @PostMapping("/{asignacionId}/notificar")
    public ResponseEntity<ApiResponse<String>> notificarEvaluadores(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long asignacionId) {

        log.info("Enviando notificaciones a evaluadores - Asignación: {}", asignacionId);

        // Solo jefe de departamento puede forzar notificaciones
        securityUtil.validateRole(userRole, EvaluatorRole.JEFE_DEPARTAMENTO);

        // Aquí debes llamar al método real en AsignacionService
        asignacionService.notificarEvaluadoresAsignados(asignacionId);

        return ResponseEntity.ok(ApiResponse.success(
                "Notificaciones enviadas exitosamente a RabbitMQ."
        ));
    }
}