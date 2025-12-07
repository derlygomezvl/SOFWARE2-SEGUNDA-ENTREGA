package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.events.NotificationPublisher;

public class ProyectoCanceladoState implements ProjectState {

    private final NotificationPublisher notificationPublisher;

    public ProyectoCanceladoState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("Proyecto cancelado. No se pueden hacer modificaciones.");
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("Proyecto cancelado. No se pueden hacer modificaciones.");
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("Proyecto cancelado. No se pueden hacer evaluaciones.");
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("Proyecto cancelado. No se pueden hacer evaluaciones.");
    }

    @Override
    public boolean puedeAvanzar() {
        return false; // Estado final
    }

    @Override
    public String getNombreEstado() {
        return "PROYECTO_CANCELADO";
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
