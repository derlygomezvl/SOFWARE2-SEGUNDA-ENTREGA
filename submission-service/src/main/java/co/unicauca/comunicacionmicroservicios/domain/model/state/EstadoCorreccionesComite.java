package co.unicauca.comunicacionmicroservicios.domain.model.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.model.enumEstadoProyecto;

public class EstadoCorreccionesComite implements IEstadoProyecto {

    @Override
    public void corregir(ProyectoGrado proyecto) {
//        proyecto.incrementarIntentos();
//        if (proyecto.getNumeroIntentos() > 3) {
//            proyecto.marcarComoRechazadoDefinitivo();
//        } else {
//            proyecto.setEstado(enumEstadoProyecto.CORRECCIONES_COMITE);
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
        throw new IllegalStateException("No se puede presentar anteproyecto en estado de correcciones");
    }
}