package com.unicauca.facade_service.dto;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * Datos JSON para crear el Formato A inicial (RF2).
 * Los archivos van en otras partes del multipart: pdf (obligatorio), carta (si PRACTICA_PROFESIONAL).
 */
public class FormatoDTO {

    private String titulo;

    private enumModalidad modalidad; // INVESTIGACION | PRACTICA_PROFESIONAL

    private String objetivoGeneral;

    // Si prefieres string largo como en el monolito, cambia a @NotBlank String objetivosEspecificos
    private List<String> objetivosEspecificos;

    // Actores: solo se guardan IDs (no objetos User)
    private Integer directorId;

    private Integer codirectorId; // opcional
    private Integer estudiante1Id;
    private Integer estudiante2Id; // opcional

    // getters/setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public enumModalidad getModalidad() { return modalidad; }
    public void setModalidad(enumModalidad modalidad) { this.modalidad = modalidad; }

    public String getObjetivoGeneral() { return objetivoGeneral; }
    public void setObjetivoGeneral(String objetivoGeneral) { this.objetivoGeneral = objetivoGeneral; }

    public List<String> getObjetivosEspecificos() { return objetivosEspecificos; }
    public void setObjetivosEspecificos(List<String> objetivosEspecificos) { this.objetivosEspecificos = objetivosEspecificos; }

    public Integer getDirectorId() { return directorId; }
    public void setDirectorId(Integer directorId) { this.directorId = directorId; }

    public Integer getCodirectorId() { return codirectorId; }
    public void setCodirectorId(Integer codirectorId) { this.codirectorId = codirectorId; }

    public Integer getEstudiante1Id() { return estudiante1Id; }
    public void setEstudiante1Id(Integer estudiante1Id) { this.estudiante1Id = estudiante1Id; }

    public Integer getEstudiante2Id() { return estudiante2Id; }
    public void setEstudiante2Id(Integer estudiante2Id) { this.estudiante2Id = estudiante2Id; }

    public MultiValueMap<String, Object> toMultiValueMap()
    {
        MultiValueMap<String, Object> objObjectMultiValueMap = new LinkedMultiValueMap<>();
        objObjectMultiValueMap.add("titulo", this.titulo);
        objObjectMultiValueMap.add("modalidad", this.modalidad.name());
        objObjectMultiValueMap.add("objetivoGeneral", this.objetivoGeneral);
        objObjectMultiValueMap.add("objetivosEspecificos", this.objetivosEspecificos);
        objObjectMultiValueMap.add("directorId", this.directorId);
        objObjectMultiValueMap.add("codirectorId", this.codirectorId);
        objObjectMultiValueMap.add("estudiante1Id", this.codirectorId);
        objObjectMultiValueMap.add("estudiante2Id", this.estudiante2Id);
        return objObjectMultiValueMap;
    }
}
