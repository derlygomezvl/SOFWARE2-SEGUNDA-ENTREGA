package co.unicauca.comunicacionmicroservicios.dto;

import co.unicauca.comunicacionmicroservicios.domain.model.enumEstadoFormato;
import java.time.LocalDateTime;

/** Vista simple de una versión de Formato A. */
public class FormatoAView {
    private Long id;
    private Long proyectoId;
    private Integer version;                  // número de intento
    private enumEstadoFormato estado;         // PENDIENTE/APROBADO/RECHAZADO
    private String observaciones;             // si ya fue evaluado
    private String nombreArchivo;             // nombre original
    private String pdfUrl;                    // URL o path (si expones)
    private String cartaUrl;                  // si aplica
    private LocalDateTime fechaEnvio;

    // Getters
    public Long getId() { return id; }
    public Long getProyectoId() { return proyectoId; }
    public Integer getVersion() { return version; }
    public enumEstadoFormato getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public String getNombreArchivo() { return nombreArchivo; }
    public String getPdfUrl() { return pdfUrl; }
    public String getCartaUrl() { return cartaUrl; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }
    public void setVersion(Integer version) { this.version = version; }
    public void setEstado(enumEstadoFormato estado) { this.estado = estado; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    public void setCartaUrl(String cartaUrl) { this.cartaUrl = cartaUrl; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}