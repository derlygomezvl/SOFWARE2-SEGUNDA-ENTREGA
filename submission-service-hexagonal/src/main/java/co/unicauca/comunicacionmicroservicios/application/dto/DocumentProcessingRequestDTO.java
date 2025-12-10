package co.unicauca.comunicacionmicroservicios.application.dto;

import java.util.Map;

public class DocumentProcessingRequestDTO {

    private String tipoDocumento;
    private String contenido;
    private String usuarioId;
    private String titulo;
    private String modalidad;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private String archivoAdjunto;
    private Map<String, Object> metadata;

    // Getters y Setters
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public String getObjetivoGeneral() { return objetivoGeneral; }
    public void setObjetivoGeneral(String objetivoGeneral) { this.objetivoGeneral = objetivoGeneral; }
    public String getObjetivosEspecificos() { return objetivosEspecificos; }
    public void setObjetivosEspecificos(String objetivosEspecificos) { this.objetivosEspecificos = objetivosEspecificos; }
    public String getArchivoAdjunto() { return archivoAdjunto; }
    public void setArchivoAdjunto(String archivoAdjunto) { this.archivoAdjunto = archivoAdjunto; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
