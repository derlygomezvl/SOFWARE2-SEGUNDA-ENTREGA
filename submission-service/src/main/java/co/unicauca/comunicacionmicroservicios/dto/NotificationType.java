package co.unicauca.comunicacionmicroservicios.dto;

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
    PROYECTO_FINALIZADO
}

