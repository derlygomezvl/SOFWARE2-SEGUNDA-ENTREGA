package com.unicauca.facade_service.dto;

/**
 * DTO para representar datos de un docente.
 * @author javiersolanop777
 */
public class DocenteDTO {
    private Long atrId;
    private String atrNombre;
    private String atrCorreo;
    private String atrDocumento;

    public DocenteDTO() {}

    public DocenteDTO(Long atrId, String atrNombre, String atrCorreo, String atrDocumento) {
        this.atrId = atrId;
        this.atrNombre = atrNombre;
        this.atrCorreo = atrCorreo;
        this.atrDocumento = atrDocumento;
    }

    public Long getAtrId() { return atrId; }
    public void setAtrId(Long atrId) { this.atrId = atrId; }

    public String getAtrNombre() { return atrNombre; }
    public void setAtrNombre(String atrNombre) { this.atrNombre = atrNombre; }

    public String getAtrCorreo() { return atrCorreo; }
    public void setAtrCorreo(String atrCorreo) { this.atrCorreo = atrCorreo; }

    public String getAtrDocumento() { return atrDocumento; }
    public void setAtrDocumento(String atrDocumento) { this.atrDocumento = atrDocumento; }
}
