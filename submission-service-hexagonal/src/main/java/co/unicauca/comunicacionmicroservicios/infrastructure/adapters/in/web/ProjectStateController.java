package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.web;

import co.unicauca.comunicacionmicroservicios.domain.ports.in.web.ProjectStateWebPort;
import co.unicauca.comunicacionmicroservicios.domain.services.ProjectStateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/project-state")
public class ProjectStateController implements ProjectStateWebPort {

    private final ProjectStateService projectStateService;

    public ProjectStateController(ProjectStateService projectStateService) {
        this.projectStateService = projectStateService;
    }

    @Override
    public ResponseEntity<Map<String, String>> manejarFormatoA(
        String proyectoId,
        Map<String, String> request
    )
    {
        String contenido = request.get("contenido");
        projectStateService.manejarFormatoA(proyectoId, contenido);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Formato A procesado exitosamente");
        response.put("proyectoId", proyectoId);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, String>> manejarAnteproyecto(
        String proyectoId,
        Map<String, String> request
    )
    {
        String contenido = request.get("contenido");
        projectStateService.manejarAnteproyecto(proyectoId, contenido);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Anteproyecto procesado exitosamente");
        response.put("proyectoId", proyectoId);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, String>> evaluarFormatoA(
        String proyectoId,
        Map<String, String> request
    )
    {
        String decision = request.get("decision");
        String observaciones = request.get("observaciones");
        projectStateService.evaluarFormatoA(proyectoId, decision, observaciones);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Formato A evaluado exitosamente");
        response.put("proyectoId", proyectoId);
        response.put("decision", decision);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, String>> evaluarAnteproyecto(
        String proyectoId,
        Map<String, String> request
    )
    {
        String decision = request.get("decision");
        String observaciones = request.get("observaciones");
        projectStateService.evaluarAnteproyecto(proyectoId, decision, observaciones);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Anteproyecto evaluado exitosamente");
        response.put("proyectoId", proyectoId);
        response.put("decision", decision);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Boolean>> obtenerPermisos(String proyectoId)
    {
        Map<String, Boolean> permisos = new HashMap<>();
        permisos.put("puedeAvanzar", projectStateService.puedeAvanzar(proyectoId));
        permisos.put("permiteReenvioFormatoA", projectStateService.permiteReenvioFormatoA(proyectoId));
        permisos.put("permiteSubirAnteproyecto", projectStateService.permiteSubirAnteproyecto(proyectoId));

        return ResponseEntity.ok(permisos);
    }
}
