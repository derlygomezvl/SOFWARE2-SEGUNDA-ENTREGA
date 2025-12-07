package co.unicauca.comunicacionmicroservicios.domain.services;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.state.ProjectStateFactory;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.db.repository.IProyectoGradoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectStateService {

    private final IProyectoGradoRepository proyectoRepository;
    private final ProjectStateFactory stateFactory;

    public ProjectStateService(IProyectoGradoRepository proyectoRepository,
                               ProjectStateFactory stateFactory) {
        this.proyectoRepository = proyectoRepository;
        this.stateFactory = stateFactory;
    }

    public ProyectoGrado obtenerProyectoConEstado(String proyectoId) {
        try {
            // Convertir String a Integer
            Integer id = Integer.parseInt(proyectoId);

            ProyectoGrado proyecto = proyectoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado: " + proyectoId));

            // ✅ CORREGIDO: Ya no necesitamos initializeState porque eliminamos los campos transient
            // El estado se maneja directamente a través de los métodos que reciben stateFactory
            return proyecto;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID de proyecto inválido: " + proyectoId + ". Debe ser un número válido.");
        }
    }

    // ✅ CORREGIDO: Métodos actualizados para pasar stateFactory

    public void manejarFormatoA(String proyectoId, String contenido) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        proyecto.manejarFormatoA(stateFactory, contenido);
        proyectoRepository.save(proyecto);
    }

    public void manejarAnteproyecto(String proyectoId, String contenido) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        proyecto.manejarAnteproyecto(stateFactory, contenido);
        proyectoRepository.save(proyecto);
    }

    public void evaluarFormatoA(String proyectoId, String decision, String observaciones) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        proyecto.evaluarFormatoA(stateFactory, decision, observaciones);
        proyectoRepository.save(proyecto);
    }

    public void evaluarAnteproyecto(String proyectoId, String decision, String observaciones) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        proyecto.evaluarAnteproyecto(stateFactory, decision, observaciones);
        proyectoRepository.save(proyecto);
    }

    public boolean puedeAvanzar(String proyectoId) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        return proyecto.puedeAvanzar(stateFactory);
    }

    public boolean permiteReenvioFormatoA(String proyectoId) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        return proyecto.permiteReenvioFormatoA(stateFactory);
    }

    public boolean permiteSubirAnteproyecto(String proyectoId) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        return proyecto.permiteSubirAnteproyecto(stateFactory);
    }

    // ✅ MÉTODOS ADICIONALES ÚTILES

    public ProyectoGrado cambiarEstadoProyecto(String proyectoId, String nuevoEstado) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);

        // Convertir String a enum (necesitarías un método helper para esto)
        // proyecto.setEstado(convertirStringAEstado(nuevoEstado));

        return proyectoRepository.save(proyecto);
    }

    public String obtenerEstadoActual(String proyectoId) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        return proyecto.getEstado().name();
    }

    public int obtenerIntentosFormatoA(String proyectoId) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        return proyecto.getIntentosFormatoA();
    }

    public boolean estaEnEstadoFinal(String proyectoId) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        return proyecto.getEstado().name().contains("FINALIZADO") ||
                proyecto.getEstado().name().contains("CANCELADO");
    }
}