package co.unicauca.comunicacionmicroservicios.domain.model.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.model.enumEstadoProyecto;

public class EstadoFormatoADiligenciado implements IEstadoProyecto {

    @Override
    public void corregir(ProyectoGrado proyecto) {
//        proyecto.setEstado(enumEstadoProyecto.CORRECCIONES_COMITE);
//        proyecto.incrementarIntentos();
//        if (proyecto.getNumeroIntentos() > 3) {
//            proyecto.setEstado(enumEstadoProyecto.RECHAZADO_DEFINITIVO);
//        }
    }

    @Override
    public void aprobar(ProyectoGrado proyecto) {
//        proyecto.setEstado(enumEstadoProyecto.ACEPTADO_POR_COMITE);
    }

    @Override
    public void rechazar(ProyectoGrado proyecto) {
//        proyecto.setEstado(enumEstadoProyecto.RECHAZADO_POR_COMITE);
    }

    @Override
    public void presentarAnteproyecto(ProyectoGrado proyecto) {
        throw new IllegalStateException("Formato A no aprobado");
    }
}
