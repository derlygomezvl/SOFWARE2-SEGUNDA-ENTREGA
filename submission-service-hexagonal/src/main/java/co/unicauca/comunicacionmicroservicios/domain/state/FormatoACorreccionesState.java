package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;

public class FormatoACorreccionesState implements ProjectState {

    private final NotificationPublisher notificationPublisher;

    public FormatoACorreccionesState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        // Permite reenviar el formato A con correcciones
        proyecto.setEstado(ProjectStateEnum.FORMATO_A_EN_EVALUACION);
        //notificationPublisher.notificarReenvioFormatoA(proyecto);
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("Debe corregir y reenviar el formato A primero.");
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El formato A requiere correcciones. Debe ser reenviado primero.");
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El anteproyecto no puede ser evaluado. Formato A requiere correcciones.");
    }

    @Override
    public boolean puedeAvanzar() {
        return false;
    }

    @Override
    public String getNombreEstado() {
        return "FORMATO_A_CORRECCIONES";
    }

    @Override
    public boolean permiteReenvioFormatoA() {
        return true; // Permite reenvío para correcciones
    }

    @Override
    public boolean permiteSubirAnteproyecto() {
        return false;
    }
}
