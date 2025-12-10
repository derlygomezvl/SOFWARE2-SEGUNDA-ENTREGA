/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.comunicacionmicroservicios.application.dto;
/**
 *
 * @author USUARIO
 */
public class SubmissionResponseDTO {
    private Integer proyectoId;
    private String estado;

    public SubmissionResponseDTO() {
    }

    public SubmissionResponseDTO(Integer proyectoId, String estado) {
        this.proyectoId = proyectoId;
        this.estado = estado;
    }

    public Integer getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Integer proyectoId) {
        this.proyectoId = proyectoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
