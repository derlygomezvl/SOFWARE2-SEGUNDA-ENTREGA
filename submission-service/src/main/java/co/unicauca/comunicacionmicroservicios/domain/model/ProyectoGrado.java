package co.unicauca.comunicacionmicroservicios.domain.model;

import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.domain.state.ProjectState;
import co.unicauca.comunicacionmicroservicios.domain.state.ProjectStateFactory;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;


@Entity
public class ProyectoGrado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String titulo;
    private ProjectStateEnum estado;
    private transient ProjectState state;
    private transient ProjectStateFactory stateFactory;
    private int intentosFormatoA;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private static final int MAX_INTENTOS = 3;

    // Constructor para nuevo proyecto
    public ProyectoGrado(String titulo, ProjectStateFactory stateFactory) {
        this.titulo = titulo;
        this.stateFactory = stateFactory;
        this.estado = ProjectStateEnum.FORMATO_A_PRESENTADO;
        this.state = stateFactory.createState(this.estado);
        this.intentosFormatoA = 1;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public ProyectoGrado() {

    }

    // Constructor para cargar desde base de datos
    public void initializeState(ProjectStateFactory stateFactory) {
        this.stateFactory = stateFactory;
        this.state = stateFactory.createState(this.estado);
    }

    // Métodos que delegan al state
    public void manejarFormatoA(String contenido) {
        state.manejarFormatoA(this, contenido);
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void manejarAnteproyecto(String contenido) {
        state.manejarAnteproyecto(this, contenido);
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void evaluarFormatoA(String decision, String observaciones) {
        state.evaluarFormatoA(this, decision, observaciones);
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void evaluarAnteproyecto(String decision, String observaciones) {
        state.evaluarAnteproyecto(this, decision, observaciones);
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public ProjectStateEnum getEstado() { return estado; }

    public void setEstado(ProjectStateEnum nuevoEstado) {
        this.estado = nuevoEstado;
        if (stateFactory != null) {
            this.state = stateFactory.createState(nuevoEstado);
        }
    }

    public int getIntentosFormatoA() { return intentosFormatoA; }

    public void incrementarIntentos() {
        this.intentosFormatoA++;
        if (this.intentosFormatoA >= MAX_INTENTOS) {
            setEstado(ProjectStateEnum.PROYECTO_CANCELADO);
        }
    }

    public boolean puedeAvanzar() {
        return state.puedeAvanzar();
    }

    public boolean permiteReenvioFormatoA() {
        return state.permiteReenvioFormatoA();
    }

    public boolean permiteSubirAnteproyecto() {
        return state.permiteSubirAnteproyecto();
    }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}