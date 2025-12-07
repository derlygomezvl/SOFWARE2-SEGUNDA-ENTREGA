package co.unicauca.comunicacionmicroservicios.domain.ports.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author javiersolanop777
 */
public interface ProjectStateWebPort {

    @PostMapping("/{proyectoId}/formato-a")
    public ResponseEntity<Map<String, String>> manejarFormatoA(
        @PathVariable String proyectoId,
        @RequestBody Map<String, String> request
    );

    @PostMapping("/{proyectoId}/anteproyecto")
    public ResponseEntity<Map<String, String>> manejarAnteproyecto(
        @PathVariable String proyectoId,
        @RequestBody Map<String, String> request
    );

    @PostMapping("/{proyectoId}/evaluar-formato-a")
    public ResponseEntity<Map<String, String>> evaluarFormatoA(
        @PathVariable String proyectoId,
        @RequestBody Map<String, String> request
    );

    @PostMapping("/{proyectoId}/evaluar-anteproyecto")
    public ResponseEntity<Map<String, String>> evaluarAnteproyecto(
        @PathVariable String proyectoId,
        @RequestBody Map<String, String> request
    );

    @GetMapping("/{proyectoId}/permisos")
    public ResponseEntity<Map<String, Boolean>> obtenerPermisos(@PathVariable String proyectoId);
}
