package co.unicauca.comunicacionmicroservicios.domain.model.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.model.enumEstadoProyecto;

public class EstadoAceptadoPorComite implements IEstadoProyecto {

    @Override
    public void corregir(ProyectoGrado proyecto) {
        // No se permiten correcciones después de aprobado
    }

    @Override
    public void aprobar(ProyectoGrado proyecto) {
        // Ya está aprobado
    }

    @Override
    public void rechazar(ProyectoGrado proyecto) {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO_POR_COMITE);
    }

    @Override
    public void presentarAnteproyecto(ProyectoGrado proyecto) {
        proyecto.setEstado(enumEstadoProyecto.ESCRIBIENDO_ANTEPROYECTO);
    }
}
