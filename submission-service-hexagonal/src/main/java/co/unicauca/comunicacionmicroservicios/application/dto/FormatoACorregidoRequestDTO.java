package co.unicauca.comunicacionmicroservicios.application.dto;

public class FormatoACorregidoRequestDTO {

    private String contenido;
    private String usuarioId;
    private String observacionesAnteriores;

    // Getters y Setters
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getObservacionesAnteriores() { return observacionesAnteriores; }
    public void setObservacionesAnteriores(String observacionesAnteriores) { this.observacionesAnteriores = observacionesAnteriores; }
}
