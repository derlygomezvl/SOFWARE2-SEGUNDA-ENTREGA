package co.unicauca.comunicacionmicroservicios.domain.model.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.model.enumEstadoProyecto;

public class EstadoRechazadoPorComite implements IEstadoProyecto {

    @Override
    public void corregir(ProyectoGrado proyecto) {
        // El coordinador ya rechazó; el docente debe reenviar
    }

    @Override
    public void aprobar(ProyectoGrado proyecto) {
        throw new IllegalStateException("No se puede aprobar un proyecto rechazado");
    }

    @Override
    public void rechazar(ProyectoGrado proyecto) {
        // Ya está rechazado
    }

    @Override
    public void presentarAnteproyecto(ProyectoGrado proyecto) {
        throw new IllegalStateException("No se puede presentar anteproyecto sin aprobar el Formato A");
    }
}
