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
@RequestMapping("/api/formatos-a")
public class FormatoAController {

    private final DocumentProcessingService documentProcessingService;
    private final ProjectStateService projectStateService;

    public FormatoAController(DocumentProcessingService documentProcessingService,
                              ProjectStateService projectStateService) {
        this.documentProcessingService = documentProcessingService;
        this.projectStateService = projectStateService;
    }

    @PostMapping
    @Operation(summary = "Subir Formato A usando Template Method y State patterns")
    public ResponseEntity<ProcessResult> subirFormatoA(@RequestBody FormatoARequest request) {
        // Validar que el proyecto existe y obtenerlo
        // Esto es un ejemplo - deberías tener lógica para crear/obtener el proyecto

        String proyectoId = generarORecuperarProyectoId(request);

        DocumentData documentData = DocumentData.createFormatoAData(
                request.getContenido(),
                request.getUsuarioId(),
                request.getTitulo(),
                request.getModalidad(),
                request.getObjetivoGeneral(),
                request.getObjetivosEspecificos(),
                request.getArchivoAdjunto()
        );

        ProcessResult result = documentProcessingService.procesarDocumento(proyectoId, documentData);

        return result.isSuccess() ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/{proyectoId}/evaluar")
    @Operation(summary = "Evaluar Formato A usando State Pattern")
    public ResponseEntity<Map<String, String>> evaluarFormatoA(
            @PathVariable String proyectoId,
            @RequestBody EvaluacionRequest request) {

        projectStateService.evaluarFormatoA(proyectoId, request.getDecision(), request.getObservaciones());

        Map<String, String> response = Map.of(
                "message", "Formato A evaluado exitosamente",
                "proyectoId", proyectoId,
                "decision", request.getDecision()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{proyectoId}/reenviar")
    @Operation(summary = "Reenviar Formato A con correcciones")
    public ResponseEntity<ProcessResult> reenviarFormatoA(
            @PathVariable String proyectoId,
            @RequestBody ReenvioRequest request) {

        DocumentData documentData = DocumentData.createFormatoACorregidoData(
                request.getContenido(),
                request.getUsuarioId(),
                request.getObservacionesAnteriores()
        );

        ProcessResult result = documentProcessingService.procesarDocumento(proyectoId, documentData);

        return result.isSuccess() ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/{proyectoId}/estado")
    @Operation(summary = "Consultar estado actual del Formato A")
    public ResponseEntity<Map<String, Object>> consultarEstado(@PathVariable String proyectoId) {
        // Aquí integrarías con tu servicio existente para obtener el estado
        Map<String, Object> estado = Map.of(
                "proyectoId", proyectoId,
                "permiteReenvio", projectStateService.permiteReenvioFormatoA(proyectoId),
                "puedeAvanzar", projectStateService.puedeAvanzar(proyectoId)
        );

        return ResponseEntity.ok(estado);
    }

    private String generarORecuperarProyectoId(FormatoARequest request) {
        // Lógica para crear nuevo proyecto o recuperar existente
        // Por ahora retornamos un ID simulado
        return "PROY_" + System.currentTimeMillis();
    }

    // DTOs internos
    public static class FormatoARequest {
        private String contenido;
        private String usuarioId;
        private String titulo;
        private String modalidad;
        private String objetivoGeneral;
        private String objetivosEspecificos;
        private String archivoAdjunto;

        // Getters y Setters
        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }
        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getModalidad() { return modalidad; }
        public void setModalidad(String modalidad) { this.modalidad = modalidad; }
        public String getObjetivoGeneral() { return objetivoGeneral; }
        public void setObjetivoGeneral(String objetivoGeneral) { this.objetivoGeneral = objetivoGeneral; }
        public String getObjetivosEspecificos() { return objetivosEspecificos; }
        public void setObjetivosEspecificos(String objetivosEspecificos) { this.objetivosEspecificos = objetivosEspecificos; }
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

    public static class ReenvioRequest {
        private String contenido;
        private String usuarioId;
        private String observacionesAnteriores;

        // Getters y Setters
        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }
        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
        public String getObservacionesAnteriores() { return observacionesAnteriores; }
        public void setObservacionesAnteriores(String observacionesAnteriores) { this.observacionesAnteriores = observacionesAnteriores; }
    }
}