package co.unicauca.comunicacionmicroservicios.domain.ports.out.events;

import co.unicauca.comunicacionmicroservicios.application.dto.NotificationRequest;
import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;

/**
 * @author javiersolanop777
 */
public interface INotificationPublisherPort {

    // ================== MÉTODOS DE NOTIFICACIÓN ==================

    public void notificarAprobacionFormatoA(ProyectoGrado proyecto);

    public void notificarRechazoFormatoA(ProyectoGrado proyecto, String observaciones);

    public void notificarCorreccionesFormatoA(ProyectoGrado proyecto, String observaciones);

    public void notificarReenvioFormatoA(ProyectoGrado proyecto);

    public void notificarRechazoDefinitivoFormatoA(ProyectoGrado proyecto);

    public void notificarAnteproyectoPresentado(ProyectoGrado proyecto);

    public void notificarAprobacionAnteproyecto(ProyectoGrado proyecto);

    public void notificarRechazoAnteproyecto(ProyectoGrado proyecto, String observaciones);

    public void notificarReenvioAnteproyecto(ProyectoGrado proyecto);

    public void notificarAsignacionEvaluadores(ProyectoGrado proyecto);

    // ================== MÉTODO BASE CORREGIDO ==================

    public void publishNotification(NotificationRequest request, String eventDescription);

    // ================== MÉTODOS ADICIONALES PARA STATE PATTERN ==================

    public void notificarFormatoAPresentado(ProyectoGrado proyecto, String contenido);
}
