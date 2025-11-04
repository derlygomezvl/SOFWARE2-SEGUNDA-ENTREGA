package co.unicauca.comunicacionmicroservicios.domain.model.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;

public interface IEstadoProyecto {
    void corregir(ProyectoGrado proyecto);
    void aprobar(ProyectoGrado proyecto);
    void rechazar(ProyectoGrado proyecto);
    void presentarAnteproyecto(ProyectoGrado proyecto);
}
