package co.unicauca.comunicacionmicroservicios.domain.state;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;

public interface ProjectState {
    void manejarFormatoA(ProyectoGrado proyecto, String contenido);
    void manejarAnteproyecto(ProyectoGrado proyecto, String contenido);
    void evaluarFormatoA(ProyectoGrado proyecto, String decision, String observaciones);
    void evaluarAnteproyecto(ProyectoGrado proyecto, String decision, String observaciones);
    boolean puedeAvanzar();
    String getNombreEstado();
    boolean permiteReenvioFormatoA();
    boolean permiteSubirAnteproyecto();
}
