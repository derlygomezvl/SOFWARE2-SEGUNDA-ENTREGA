package co.unicauca.comunicacionmicroservicios.domain.enums;

public enum ProjectStateEnum {
    // Estados para Formato A
    FORMATO_A_PRESENTADO("Formato A Presentado"),
    FORMATO_A_EN_EVALUACION("Formato A en Evaluación"),
    FORMATO_A_ACEPTADO("Formato A Aceptado"),
    FORMATO_A_RECHAZADO("Formato A Rechazado"),
    FORMATO_A_CORRECCIONES("Formato A Requiere Correcciones"),

    // Estados para Anteproyecto
    ANTEPROYECTO_PRESENTADO("Anteproyecto Presentado"),
    ANTEPROYECTO_EN_EVALUACION("Anteproyecto en Evaluación"),
    ANTEPROYECTO_ASIGNADO("Evaluadores Asignados"),
    ANTEPROYECTO_ACEPTADO("Anteproyecto Aceptado"),
    ANTEPROYECTO_RECHAZADO("Anteproyecto Rechazado"),

    // Estados finales
    PROYECTO_FINALIZADO("Proyecto Finalizado"),
    PROYECTO_CANCELADO("Proyecto Cancelado");

    private final String descripcion;

    ProjectStateEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
