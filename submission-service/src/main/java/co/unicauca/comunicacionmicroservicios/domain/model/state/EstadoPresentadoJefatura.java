package co.unicauca.comunicacionmicroservicios.domain.model.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.model.enumEstadoProyecto;

public class EstadoPresentadoJefatura implements IEstadoProyecto {

    @Override
    public void corregir(ProyectoGrado proyecto) {
        // La jefatura no corrige; asigna evaluadores
    }

    @Override
    public void aprobar(ProyectoGrado proyecto) {
        // La jefatura no aprueba directamente
    }

    @Override
    public void rechazar(ProyectoGrado proyecto) {
        // La jefatura no rechaza directamente
    }

    @Override
    public void presentarAnteproyecto(ProyectoGrado proyecto) {
        // Ya fue presentado
    }
}
