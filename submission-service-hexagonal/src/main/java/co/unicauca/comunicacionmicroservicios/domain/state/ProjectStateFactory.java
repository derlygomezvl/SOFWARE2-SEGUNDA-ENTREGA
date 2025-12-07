package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.events.NotificationPublisher;
import org.springframework.stereotype.Component;

@Component
public class ProjectStateFactory {

    private final NotificationPublisher notificationPublisher;

    public ProjectStateFactory(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    public ProjectState createState(ProjectStateEnum estado) {
        switch (estado) {
            case FORMATO_A_PRESENTADO:
                return new FormatoAPresentadoState(notificationPublisher);
            case FORMATO_A_EN_EVALUACION:
                return new FormatoAEnEvaluacionState(notificationPublisher);
            case FORMATO_A_ACEPTADO:
                return new FormatoAAceptadoState(notificationPublisher);
            case FORMATO_A_RECHAZADO:
                return new FormatoARechazadoState(notificationPublisher);
            case FORMATO_A_CORRECCIONES:
                return new FormatoACorreccionesState(notificationPublisher);
            case ANTEPROYECTO_PRESENTADO:
                return new AnteproyectoPresentadoState(notificationPublisher);
            case ANTEPROYECTO_EN_EVALUACION:
                return new AnteproyectoEnEvaluacionState(notificationPublisher);
            case ANTEPROYECTO_ASIGNADO:
                return new AnteproyectoAsignadoState(notificationPublisher);
            case ANTEPROYECTO_ACEPTADO:
                return new AnteproyectoAceptadoState(notificationPublisher);
            case ANTEPROYECTO_RECHAZADO:
                return new AnteproyectoRechazadoState(notificationPublisher);
            case PROYECTO_FINALIZADO:
                return new ProyectoFinalizadoState(notificationPublisher);
            case PROYECTO_CANCELADO:
                return new ProyectoCanceladoState(notificationPublisher);
            default:
                throw new IllegalArgumentException("Estado no soportado: " + estado);
        }
    }
}
