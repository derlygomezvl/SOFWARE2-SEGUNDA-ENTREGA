package co.unicauca.comunicacionmicroservicios.application.dto;

/**
 * Tipos de notificaciones soportadas por el sistema.
 * DEBE coincidir con el enum del notification-service.
 */
public enum NotificationType {
    FORMATO_A_PRESENTADO,
    FORMATO_A_APROBADO,
    FORMATO_A_RECHAZADO,
    FORMATO_A_CORRECCIONES,
    FORMATO_A_REENVIADO,
    ANTEPROYECTO_PRESENTADO,
    ANTEPROYECTO_APROBADO,
    ANTEPROYECTO_RECHAZADO,
    ANTEPROYECTO_REENVIADO,
    EVALUADORES_ASIGNADOS,
    PROYECTO_CANCELADO,
    PROYECTO_FINALIZADO,
    /**
     * Notificación cuando se envía un Formato A (inicial o nueva versión)
     * Destinatario: Coordinador
     */
    DOCUMENT_SUBMITTED,

    /**
     * Notificación cuando se completa la evaluación de un documento
     * Destinatarios: Docente(s) y Estudiante(s)
     */
    EVALUATION_COMPLETED,

    /**
     * Notificación cuando cambia el estado de un proyecto
     * Destinatarios: Involucrados en el proyecto
     */
    STATUS_CHANGED,

    /**
     * Notificación cuando se asignan evaluadores
     * Destinatarios: Evaluadores asignados
     */
    EVALUATOR_ASSIGNED,

    /**
     * Recordatorio de fecha límite
     * Destinatarios: Responsables
     */
    DEADLINE_REMINDER
}

