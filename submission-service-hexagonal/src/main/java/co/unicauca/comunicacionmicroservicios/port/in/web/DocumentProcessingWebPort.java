package co.unicauca.comunicacionmicroservicios.port.in.web;

import co.unicauca.comunicacionmicroservicios.adapter.in.web.DocumentProcessingController;
import co.unicauca.comunicacionmicroservicios.service.template.ProcessResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @author javiersolanop777
 */
public interface DocumentProcessingWebPort {

    @PostMapping("/proyecto/{proyectoId}/procesar")
//    @Operation(summary = "Procesar documento usando Template Method Pattern")
    public ResponseEntity<ProcessResult> procesarDocumento(
            @PathVariable String proyectoId,
            DocumentProcessingController.DocumentProcessingRequest request
    );

    @PostMapping("/proyecto/{proyectoId}/formato-a")
//    @Operation(summary = "Procesar Formato A (endpoint específico)")
    public ResponseEntity<ProcessResult> procesarFormatoA(
            @PathVariable String proyectoId,
            DocumentProcessingController.FormatoARequest request
    );

    @PostMapping("/proyecto/{proyectoId}/anteproyecto")
//    @Operation(summary = "Procesar Anteproyecto (endpoint específico)")
    public ResponseEntity<ProcessResult> procesarAnteproyecto(
            @PathVariable String proyectoId,
            DocumentProcessingController.AnteproyectoRequest request
    );

    @PostMapping("/proyecto/{proyectoId}/formato-a/corregido")
//    @Operation(summary = "Reenviar Formato A con correcciones")
    public ResponseEntity<ProcessResult> reenviarFormatoACorregido(
            @PathVariable String proyectoId,
            DocumentProcessingController.FormatoACorregidoRequest request
    );

    @GetMapping("/proyecto/{proyectoId}/permisos")
//    @Operation(summary = "Obtener permisos actuales del proyecto usando State Pattern")
    public ResponseEntity<Map<String, Object>> obtenerPermisos(@PathVariable String proyectoId);

    @GetMapping("/tipos-documento")
//    @Operation(summary = "Obtener tipos de documento soportados")
    public ResponseEntity<Map<String, String>> obtenerTiposDocumento();
}
