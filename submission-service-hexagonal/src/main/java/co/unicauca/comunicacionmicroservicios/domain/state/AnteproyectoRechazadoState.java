package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;

public class AnteproyectoRechazadoState implements ProjectState {

    private final NotificationPublisher notificationPublisher;

    public AnteproyectoRechazadoState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El formato A ya fue procesado.");
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        // Permite reenviar el anteproyecto después de rechazo
        proyecto.setEstado(ProjectStateEnum.ANTEPROYECTO_PRESENTADO);
        //notificationPublisher.notificarReenvioAnteproyecto(proyecto);
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El formato A ya fue evaluado.");
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El anteproyecto ya fue evaluado y rechazado.");
    }

    @Override
    public boolean puedeAvanzar() {
        return false;
    }

    @Override
    public String getNombreEstado() {
        return "ANTEPROYECTO_RECHAZADO";
    }

    @Override
    public boolean permiteReenvioFormatoA() {
        return false;
    }

    @Override
    public boolean permiteSubirAnteproyecto() {
        return true; // Permite reenvío del anteproyecto
    }
}
