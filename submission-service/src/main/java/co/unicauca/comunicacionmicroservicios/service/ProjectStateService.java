package co.unicauca.comunicacionmicroservicios.service;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.state.ProjectStateFactory;
import co.unicauca.comunicacionmicroservicios.infrastructure.repository.IProyectoGradoRepository;
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
        ProyectoGrado proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado: " + proyectoId));

        // Inicializar el estado del proyecto
        proyecto.initializeState(stateFactory);
        return proyecto;
    }

    public void manejarFormatoA(String proyectoId, String contenido) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        proyecto.manejarFormatoA(contenido);
        proyectoRepository.save(proyecto);
    }

    public void manejarAnteproyecto(String proyectoId, String contenido) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        proyecto.manejarAnteproyecto(contenido);
        proyectoRepository.save(proyecto);
    }

    public void evaluarFormatoA(String proyectoId, String decision, String observaciones) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        proyecto.evaluarFormatoA(decision, observaciones);
        proyectoRepository.save(proyecto);
    }

    public void evaluarAnteproyecto(String proyectoId, String decision, String observaciones) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        proyecto.evaluarAnteproyecto(decision, observaciones);
        proyectoRepository.save(proyecto);
    }

    public boolean puedeAvanzar(String proyectoId) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        return proyecto.puedeAvanzar();
    }

    public boolean permiteReenvioFormatoA(String proyectoId) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        return proyecto.permiteReenvioFormatoA();
    }

    public boolean permiteSubirAnteproyecto(String proyectoId) {
        ProyectoGrado proyecto = obtenerProyectoConEstado(proyectoId);
        return proyecto.permiteSubirAnteproyecto();
    }
}