package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;

public class AnteproyectoAsignadoState implements ProjectState {

    private final NotificationPublisher notificationPublisher;

    public AnteproyectoAsignadoState(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void manejarFormatoA(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El formato A ya fue procesado.");
    }

    @Override
    public void manejarAnteproyecto(ProyectoGrado proyecto, String contenido) {
        throw new IllegalStateException("El anteproyecto ya fue presentado y evaluadores asignados.");
    }

    @Override
    public void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones) {
        throw new IllegalStateException("El formato A ya fue evaluado.");
    }

    @Override
    public void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones) {
        // Permite la evaluación ahora que los evaluadores están asignados
        proyecto.setEstado(ProjectStateEnum.ANTEPROYECTO_EN_EVALUACION);
        // La evaluación real se hará en el estado ANTEPROYECTO_EN_EVALUACION
        // Este estado es transitorio
    }

    @Override
    public boolean puedeAvanzar() {
        return true; // Puede avanzar a evaluación
    }

    @Override
    public String getNombreEstado() {
        return "ANTEPROYECTO_ASIGNADO";
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
