package co.unicauca.comunicacionmicroservicios.controller;

import co.unicauca.comunicacionmicroservicios.service.DocumentProcessingService;
import co.unicauca.comunicacionmicroservicios.service.ProjectStateService;
import co.unicauca.comunicacionmicroservicios.service.template.DocumentData;
import co.unicauca.comunicacionmicroservicios.service.template.ProcessResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/anteproyectos")
public class AnteproyectoController {

    private final DocumentProcessingService documentProcessingService;
    private final ProjectStateService projectStateService;

    public AnteproyectoController(DocumentProcessingService documentProcessingService,
                                  ProjectStateService projectStateService) {
        this.documentProcessingService = documentProcessingService;
        this.projectStateService = projectStateService;
    }

    @PostMapping("/proyecto/{proyectoId}")
    @Operation(summary = "Subir anteproyecto usando Template Method y State patterns")
    public ResponseEntity<ProcessResult> subirAnteproyecto(
            @PathVariable String proyectoId,
            @RequestBody AnteproyectoRequest request) {

        // Validar que el proyecto permite subir anteproyecto
        if (!projectStateService.permiteSubirAnteproyecto(proyectoId)) {
            return ResponseEntity.badRequest().body(
                    ProcessResult.error("No se puede subir anteproyecto en el estado actual", proyectoId)
            );
        }

        DocumentData documentData = DocumentData.createAnteproyectoData(
                request.getContenido(),
                request.getUsuarioId(),
                request.getTitulo(),
                request.getArchivoAdjunto()
        );

        ProcessResult result = documentProcessingService.procesarDocumento(proyectoId, documentData);

        return result.isSuccess() ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/{proyectoId}/evaluar")
    @Operation(summary = "Evaluar anteproyecto usando State Pattern")
    public ResponseEntity<Map<String, String>> evaluarAnteproyecto(
            @PathVariable String proyectoId,
            @RequestBody EvaluacionRequest request) {

        projectStateService.evaluarAnteproyecto(proyectoId, request.getDecision(), request.getObservaciones());

        Map<String, String> response = Map.of(
                "message", "Anteproyecto evaluado exitosamente",
                "proyectoId", proyectoId,
                "decision", request.getDecision()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{proyectoId}/permisos")
    @Operation(summary = "Consultar permisos para anteproyecto")
    public ResponseEntity<Map<String, Object>> consultarPermisos(@PathVariable String proyectoId) {
        Map<String, Object> permisos = Map.of(
                "proyectoId", proyectoId,
                "permiteSubirAnteproyecto", projectStateService.permiteSubirAnteproyecto(proyectoId),
                "puedeAvanzar", projectStateService.puedeAvanzar(proyectoId)
        );

        return ResponseEntity.ok(permisos);
    }

    // DTOs internos
    public static class AnteproyectoRequest {
        private String contenido;
        private String usuarioId;
        private String titulo;
        private String archivoAdjunto;

        // Getters y Setters
        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }
        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getArchivoAdjunto() { return archivoAdjunto; }
        public void setArchivoAdjunto(String archivoAdjunto) { this.archivoAdjunto = archivoAdjunto; }
    }

    public static class EvaluacionRequest {
        private String decision;
        private String observaciones;

        // Getters y Setters
        public String getDecision() { return decision; }
        public void setDecision(String decision) { this.decision = decision; }
        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }
}
