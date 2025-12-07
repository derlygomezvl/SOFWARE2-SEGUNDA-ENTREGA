package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;

public class FormatoARechazadoState implements ProjectState {

    private final NotificationPublisher notificationPublisher;

    public FormatoARechazadoState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        // Permite reenviar el formato A si no ha excedido los intentos
        if (proyecto.getIntentosFormatoA() >= 3) {
            proyecto.setEstado(ProjectStateEnum.PROYECTO_CANCELADO);
            throw new IllegalStateException("Límite de intentos excedido. Proyecto cancelado.");
        }

        proyecto.setEstado(ProjectStateEnum.FORMATO_A_PRESENTADO);
        //notificationPublisher.notificarReenvioFormatoA(proyecto);
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("No se puede subir anteproyecto. Formato A rechazado.");
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El formato A ya fue evaluado y rechazado. Debe reenviarlo.");
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El anteproyecto no puede ser evaluado. Formato A rechazado.");
    }

    @Override
    public boolean puedeAvanzar() {
        return false;
    }

    @Override
    public String getNombreEstado() {
        return "FORMATO_A_RECHAZADO";
    }

    @Override
    public boolean permiteReenvioFormatoA() {
        return true; // Permite reenvío después de rechazo
    }

    @Override
    public boolean permiteSubirAnteproyecto() {
        return false;
    }
}
