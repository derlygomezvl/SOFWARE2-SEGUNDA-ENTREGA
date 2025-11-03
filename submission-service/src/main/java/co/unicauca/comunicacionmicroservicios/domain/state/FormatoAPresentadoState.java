package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;

public class FormatoAPresentadoState implements ProjectState {

    private final NotificationPublisher notificationPublisher;

    public FormatoAPresentadoState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El formato A ya fue presentado. Estado actual: " + getNombreEstado());
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("Debe aprobarse el formato A antes de subir el anteproyecto");
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        if ("APROBADO".equalsIgnoreCase(decision)) {
            proyecto.setEstado(ProjectStateEnum.FORMATO_A_ACEPTADO);
            notificationPublisher.notificarAprobacionFormatoA(proyecto);
        } else if ("RECHAZADO".equalsIgnoreCase(decision)) {
            proyecto.setEstado(ProjectStateEnum.FORMATO_A_RECHAZADO);
            proyecto.incrementarIntentos();
            notificationPublisher.notificarRechazoFormatoA(proyecto, observaciones);
        } else if ("CORRECCIONES".equalsIgnoreCase(decision)) {
            proyecto.setEstado(ProjectStateEnum.FORMATO_A_CORRECCIONES);
            notificationPublisher.notificarCorreccionesFormatoA(proyecto, observaciones);
        } else {
            throw new IllegalArgumentException("Decisión no válida: " + decision);
        }
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El anteproyecto no ha sido presentado aún");
    }

    @Override
    public boolean puedeAvanzar() {
        return false; // Esperando evaluación del coordinador
    }

    @Override
    public String getNombreEstado() {
        return "FORMATO_A_PRESENTADO";
    }

    @Override
    public boolean permiteReenvioFormatoA() {
        return false; // Ya está presentado, no permite reenvío
    }

    @Override
    public boolean permiteSubirAnteproyecto() {
        return false; // No permite subir anteproyecto hasta que se apruebe formato A
    }
}
