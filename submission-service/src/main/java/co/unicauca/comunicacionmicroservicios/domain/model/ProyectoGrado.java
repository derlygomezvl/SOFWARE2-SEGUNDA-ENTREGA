package co.unicauca.comunicacionmicroservicios.domain.model;

import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.domain.state.ProjectState;
import co.unicauca.comunicacionmicroservicios.domain.state.ProjectStateFactory;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proyecto_grado")
public class ProyectoGrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;

    @Enumerated(EnumType.STRING)
    private ProjectStateEnum estado;

    private int intentosFormatoA;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    private static final int MAX_INTENTOS = 3;

    // ✅ ELIMINADOS los campos transient que causaban NullPointerException
    // ❌ NO USAR: private transient ProjectState state;
    // ❌ NO USAR: private transient ProjectStateFactory stateFactory;

    // ========== CONSTRUCTORES ==========

    public ProyectoGrado() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado = ProjectStateEnum.FORMATO_A_PRESENTADO;
        this.intentosFormatoA = 1;
    }

    // Constructor para nuevo proyecto (compatible con tu SubmissionService)
    public ProyectoGrado(String titulo, ProjectStateFactory stateFactory) {
        this();
        this.titulo = titulo;
        // El estado ya se inicializa en el constructor por defecto
    }

    // ========== MÉTODOS QUE USAN STATE PATTERN ==========
    // ✅ CORREGIDOS: Reciben stateFactory como parámetro para evitar NullPointerException

    public void manejarFormatoA(ProjectStateFactory stateFactory, String contenido) {
        ProjectState currentState = stateFactory.createState(this.estado);
        currentState.manejarFormatoA(this, contenido);
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void manejarAnteproyecto(ProjectStateFactory stateFactory, String contenido) {
        ProjectState currentState = stateFactory.createState(this.estado);
        currentState.manejarAnteproyecto(this, contenido);
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void evaluarFormatoA(ProjectStateFactory stateFactory, String decision, String observaciones) {
        ProjectState currentState = stateFactory.createState(this.estado);
        currentState.evaluarFormatoA(this, decision, observaciones);
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void evaluarAnteproyecto(ProjectStateFactory stateFactory, String decision, String observaciones) {
        ProjectState currentState = stateFactory.createState(this.estado);
        currentState.evaluarAnteproyecto(this, decision, observaciones);
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ========== MÉTODOS DE CONSULTA QUE USAN STATE ==========
    // ✅ CORREGIDOS: Reciben stateFactory como parámetro

    public boolean puedeAvanzar(ProjectStateFactory stateFactory) {
        ProjectState currentState = stateFactory.createState(this.estado);
        return currentState.puedeAvanzar();
    }

    public boolean permiteReenvioFormatoA(ProjectStateFactory stateFactory) {
        ProjectState currentState = stateFactory.createState(this.estado);
        return currentState.permiteReenvioFormatoA();
    }

    public boolean permiteSubirAnteproyecto(ProjectStateFactory stateFactory) {
        ProjectState currentState = stateFactory.createState(this.estado);
        return currentState.permiteSubirAnteproyecto();
    }

    // ========== MÉTODOS DE DOMINIO ==========

    public void incrementarIntentos() {
        this.intentosFormatoA++;
        this.fechaActualizacion = LocalDateTime.now();

        // Lógica de negocio: máximo 3 intentos
        if (this.intentosFormatoA >= MAX_INTENTOS) {
            this.estado = ProjectStateEnum.PROYECTO_CANCELADO;
        }
    }

    // ========== GETTERS Y SETTERS ==========

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

    public ProjectStateEnum getEstado() {
        return estado;
    }

    public void setEstado(ProjectStateEnum nuevoEstado) {
        this.estado = nuevoEstado;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public int getIntentosFormatoA() {
        return intentosFormatoA;
    }

    public void setIntentosFormatoA(int intentosFormatoA) {
        this.intentosFormatoA = intentosFormatoA;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // ========== MÉTODOS DE CONVENIENCIA ==========

    @Override
    public String toString() {
        return "ProyectoGrado{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", estado=" + estado +
                ", intentosFormatoA=" + intentosFormatoA +
                '}';
    }
}