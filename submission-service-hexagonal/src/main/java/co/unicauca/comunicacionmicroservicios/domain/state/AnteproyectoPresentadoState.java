package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.events.NotificationPublisher;

public class AnteproyectoPresentadoState implements ProjectState {

    private final NotificationPublisher notificationPublisher;

    public AnteproyectoPresentadoState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El formato A ya fue procesado. No se puede modificar.");
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El anteproyecto ya fue presentado.");
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El formato A ya fue evaluado.");
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        if ("APROBADO".equalsIgnoreCase(decision)) {
            proyecto.setEstado(ProjectStateEnum.ANTEPROYECTO_ACEPTADO);
            //notificationPublisher.notificarAprobacionAnteproyecto(proyecto);
        } else if ("RECHAZADO".equalsIgnoreCase(decision)) {
            proyecto.setEstado(ProjectStateEnum.ANTEPROYECTO_RECHAZADO);
            //notificationPublisher.notificarRechazoAnteproyecto(proyecto, observaciones);
        } else {
            throw new IllegalArgumentException("Decisión no válida: " + decision);
        }
    }

    @Override
    public boolean puedeAvanzar() {
        return false; // Esperando evaluación
    }

    @Override
    public String getNombreEstado() {
        return "ANTEPROYECTO_PRESENTADO";
    }

    @Override
    public boolean permiteReenvioFormatoA() {
        return false;
    }

    @Override
    public boolean permiteSubirAnteproyecto() {
        return false; // Ya está presentado
    }
}
