package co.unicauca.comunicacionmicroservicios.domain.model.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.model.enumEstadoProyecto;

public class EstadoEscribiendoAnteproyecto implements IEstadoProyecto {

    @Override
    public void corregir(ProyectoGrado proyecto) {
        // No aplica
    }

    @Override
    public void aprobar(ProyectoGrado proyecto) {
        // No aplica
    }

    @Override
    public void rechazar(ProyectoGrado proyecto) {
        // No aplica
    }

    @Override
    public void presentarAnteproyecto(ProyectoGrado proyecto) {
//        proyecto.setEstado(enumEstadoProyecto.PRESENTADO_JEFATURA);
    }
}
