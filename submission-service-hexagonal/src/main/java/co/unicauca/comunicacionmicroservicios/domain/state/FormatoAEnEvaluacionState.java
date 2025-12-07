package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;

public class FormatoAEnEvaluacionState implements ProjectState {

    private final NotificationPublisher notificationPublisher;

    public FormatoAEnEvaluacionState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El formato A está en evaluación. No se puede modificar.");
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("Debe esperar la evaluación del formato A antes de subir el anteproyecto.");
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        if ("APROBADO".equalsIgnoreCase(decision)) {
            proyecto.setEstado(ProjectStateEnum.FORMATO_A_ACEPTADO);
            //notificationPublisher.notificarAprobacionFormatoA(proyecto);
        } else if ("RECHAZADO".equalsIgnoreCase(decision)) {
            proyecto.setEstado(ProjectStateEnum.FORMATO_A_RECHAZADO);
            proyecto.incrementarIntentos();
            //notificationPublisher.notificarRechazoFormatoA(proyecto, observaciones);
        } else if ("CORRECCIONES".equalsIgnoreCase(decision)) {
            proyecto.setEstado(ProjectStateEnum.FORMATO_A_CORRECCIONES);
            //notificationPublisher.notificarCorreccionesFormatoA(proyecto, observaciones);
        } else {
            throw new IllegalArgumentException("Decisión no válida: " + decision);
        }
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El anteproyecto no ha sido presentado aún.");
    }

    @Override
    public boolean puedeAvanzar() {
        return false; // En evaluación, no puede avanzar
    }

    @Override
    public String getNombreEstado() {
        return "FORMATO_A_EN_EVALUACION";
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