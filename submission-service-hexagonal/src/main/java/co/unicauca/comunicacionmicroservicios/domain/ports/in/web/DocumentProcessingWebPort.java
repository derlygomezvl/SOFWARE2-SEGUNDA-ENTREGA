package co.unicauca.comunicacionmicroservicios.domain.ports.in.web;

import co.unicauca.comunicacionmicroservicios.application.dto.AnteproyectoRequestDTO;
import co.unicauca.comunicacionmicroservicios.application.dto.DocumentProcessingRequestDTO;
import co.unicauca.comunicacionmicroservicios.application.dto.FormatoACorregidoRequestDTO;
import co.unicauca.comunicacionmicroservicios.application.dto.FormatoARequestDTO;
import co.unicauca.comunicacionmicroservicios.domain.services.template.ProcessResult;
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
            DocumentProcessingRequestDTO request
    );

    @PostMapping("/proyecto/{proyectoId}/formato-a")
//    @Operation(summary = "Procesar Formato A (endpoint específico)")
    public ResponseEntity<ProcessResult> procesarFormatoA(
            @PathVariable String proyectoId,
            FormatoARequestDTO request
    );

    @PostMapping("/proyecto/{proyectoId}/anteproyecto")
//    @Operation(summary = "Procesar Anteproyecto (endpoint específico)")
    public ResponseEntity<ProcessResult> procesarAnteproyecto(
            @PathVariable String proyectoId,
            AnteproyectoRequestDTO request
    );

    @PostMapping("/proyecto/{proyectoId}/formato-a/corregido")
//    @Operation(summary = "Reenviar Formato A con correcciones")
    public ResponseEntity<ProcessResult> reenviarFormatoACorregido(
            @PathVariable String proyectoId,
            FormatoACorregidoRequestDTO request
    );

    @GetMapping("/proyecto/{proyectoId}/permisos")
//    @Operation(summary = "Obtener permisos actuales del proyecto usando State Pattern")
    public ResponseEntity<Map<String, Object>> obtenerPermisos(@PathVariable String proyectoId);

    @GetMapping("/tipos-documento")
//    @Operation(summary = "Obtener tipos de documento soportados")
    public ResponseEntity<Map<String, String>> obtenerTiposDocumento();
}
