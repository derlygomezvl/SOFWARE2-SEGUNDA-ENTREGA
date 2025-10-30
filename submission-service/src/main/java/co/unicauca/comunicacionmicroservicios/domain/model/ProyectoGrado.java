/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.comunicacionmicroservicios.domain.model;

/**
 *
 * @author USUARIO
 */

import co.unicauca.comunicacionmicroservicios.domain.model.state.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "proyectos_grado")
public class ProyectoGrado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;

    @Enumerated(EnumType.STRING)
    private enumModalidad modalidad;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private Integer directorId;
    private Integer codirectorId;

    @Column(columnDefinition = "text")
    private String objetivoGeneral;

    @Column(columnDefinition = "text")
    private String objetivosEspecificos;

    private Integer estudiante1Id;
    private Integer estudiante2Id;

    @Enumerated(EnumType.STRING)
    private enumEstadoProyecto estado = enumEstadoProyecto.EN_DESARROLLO;

    private Integer numeroIntentos = 1;

    // Relación con FormatoA (OneToMany por intentos)
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormatoA> formatos;

    public boolean puedeSubirNuevaVersion() {
        return estado == enumEstadoProyecto.RECHAZADO_POR_COMITE && numeroIntentos < 3;
    }
    public void marcarComoRechazadoDefinitivo() { this.estado = enumEstadoProyecto.RECHAZADO_DEFINITIVO; }
    public void incrementarIntentos() { this.numeroIntentos = this.numeroIntentos + 1; }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public enumModalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(enumModalidad modalidad) {
        this.modalidad = modalidad;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Integer directorId) {
        this.directorId = directorId;
    }

    public Integer getCodirectorId() {
        return codirectorId;
    }

    public void setCodirectorId(Integer codirectorId) {
        this.codirectorId = codirectorId;
    }

    public String getObjetivoGeneral() {
        return objetivoGeneral;
    }

    public void setObjetivoGeneral(String objetivoGeneral) {
        this.objetivoGeneral = objetivoGeneral;
    }

    public String getObjetivosEspecificos() {
        return objetivosEspecificos;
    }

    public void setObjetivosEspecificos(String objetivosEspecificos) {
        this.objetivosEspecificos = objetivosEspecificos;
    }

    public Integer getEstudiante1Id() {
        return estudiante1Id;
    }

    public void setEstudiante1Id(Integer estudiante1Id) {
        this.estudiante1Id = estudiante1Id;
    }

    public Integer getEstudiante2Id() {
        return estudiante2Id;
    }

    public void setEstudiante2Id(Integer estudiante2Id) {
        this.estudiante2Id = estudiante2Id;
    }

    public enumEstadoProyecto getEstado() {
        return estado;
    }

    public void setEstado(enumEstadoProyecto estado) {
        this.estado = estado;
    }

    public Integer getNumeroIntentos() {
        return numeroIntentos;
    }

    public void setNumeroIntentos(Integer numeroIntentos) {
        this.numeroIntentos = numeroIntentos;
    }

    public List<FormatoA> getFormatos() {
        return formatos;
    }

    public void setFormatos(List<FormatoA> formatos) {
        this.formatos = formatos;
    }

    public IEstadoProyecto obtenerEstadoActual() {
        return switch (this.estado) {
            case FORMATO_A_DILIGENCIADO -> new EstadoFormatoADiligenciado();
            case CORRECCIONES_COMITE -> new EstadoCorreccionesComite();
            case ACEPTADO_POR_COMITE -> new EstadoAceptadoPorComite();
            case RECHAZADO_POR_COMITE, RECHAZADO_DEFINITIVO -> new EstadoRechazadoPorComite();
            case ESCRIBIENDO_ANTEPROYECTO -> new EstadoEscribiendoAnteproyecto();
            case PRESENTADO_JEFATURA -> new EstadoPresentadoJefatura();
            default -> throw new IllegalStateException("Estado no soportado: " + this.estado);
        };
    }

}

