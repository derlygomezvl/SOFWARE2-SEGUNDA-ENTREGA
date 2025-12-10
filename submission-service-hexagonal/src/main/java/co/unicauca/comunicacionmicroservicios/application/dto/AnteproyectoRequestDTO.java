package co.unicauca.comunicacionmicroservicios.application.dto;

public class AnteproyectoRequestDTO {

    private String contenido;
    private String usuarioId;
    private String titulo;
    private String archivoAdjunto;

    // Getters y Setters
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getArchivoAdjunto() { return archivoAdjunto; }
    public void setArchivoAdjunto(String archivoAdjunto) { this.archivoAdjunto = archivoAdjunto; }
}
