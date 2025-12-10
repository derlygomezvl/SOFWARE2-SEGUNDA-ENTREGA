package co.unicauca.comunicacionmicroservicios.application.dto;

import jakarta.validation.constraints.NotNull;

/** Datos JSON para subir el anteproyecto (RF6). */
public class AnteproyectoDataDTO {

    @NotNull
    private Long proyectoId;

    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }
}
