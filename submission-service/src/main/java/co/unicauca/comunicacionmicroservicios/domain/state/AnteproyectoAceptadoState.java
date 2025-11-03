package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;

public class AnteproyectoAceptadoState implements ProjectState {

    private final NotificationPublisher notificationPublisher;

    public AnteproyectoAceptadoState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El formato A ya fue procesado.");
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El anteproyecto ya fue aceptado.");
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El formato A ya fue evaluado.");
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El anteproyecto ya fue evaluado y aceptado.");
    }

    @Override
    public boolean puedeAvanzar() {
        return true; // Puede avanzar a desarrollo del proyecto
    }

    @Override
    public String getNombreEstado() {
        return "ANTEPROYECTO_ACEPTADO";
    }

    @Override
    public boolean permiteReenvioFormatoA() {
        return false;
    }

    @Override
    public boolean permiteSubirAnteproyecto() {
        return false;
    }
}
