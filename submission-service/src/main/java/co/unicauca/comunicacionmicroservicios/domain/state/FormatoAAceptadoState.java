package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;

public class FormatoAAceptadoState implements ProjectState  {

    private final NotificationPublisher notificationPublisher;

    public FormatoAAceptadoState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El formato A ya fue aceptado. No se puede modificar.");
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        // Permite subir el anteproyecto ahora que el formato A está aceptado
        proyecto.setEstado(ProjectStateEnum.ANTEPROYECTO_PRESENTADO);
        notificationPublisher.notificarAnteproyectoPresentado(proyecto);
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El formato A ya fue evaluado y aceptado");
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El anteproyecto no ha sido presentado aún");
    }

    @Override
    public boolean puedeAvanzar() {
        return true; // Permite avanzar al anteproyecto
    }

    @Override
    public String getNombreEstado() {
        return "FORMATO_A_ACEPTADO";
    }

    @Override
    public boolean permiteReenvioFormatoA() {
        return false;
    }

    @Override
    public boolean permiteSubirAnteproyecto() {
        return true; // ¡Importante! Permite subir anteproyecto
    }
}
