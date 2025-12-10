package co.unicauca.review.service;

import co.unicauca.review.dto.response.NotificationEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Simula el envío de notificación por email
     */
    public void enviarNotificacionAsignacion(
            Long anteproyectoId,
            String tituloAnteproyecto,
            Long evaluador1Id,
            Long evaluador2Id) {

        log.info("🚀 ENVIANDO NOTIFICACIÓN DE ASIGNACIÓN");
        log.info("========================================");
        log.info("Anteproyecto: {} (ID: {})", tituloAnteproyecto, anteproyectoId);
        log.info("Evaluador 1: {}@unicauca.edu.co", evaluador1Id);
        log.info("Evaluador 2: {}@unicauca.edu.co", evaluador2Id);
        log.info("Mensaje: Has sido asignado como evaluador de un anteproyecto.");
        log.info("Por favor, inicia sesión en el sistema para revisar los detalles.");
        log.info("========================================");
    }

    /**
     * Crea evento de notificación para RabbitMQ (simulado)
     */
    public NotificationEventDTO crearEventoAsignacion(
            Long anteproyectoId,
            String tituloAnteproyecto,
            Long evaluador1Id,
            Long evaluador2Id) {

        return NotificationEventDTO.builder()
                .eventType("EVALUADORES_ASIGNADOS")
                .documentId(anteproyectoId)
                .documentTitle(tituloAnteproyecto)
                .documentType("ANTEPROYECTO")
                .decision(null)
                .evaluatorName("Sistema de Gestión")
                .evaluatorRole("JEFE_DEPARTAMENTO")
                .observaciones("Evaluadores asignados para revisión")
                .recipients(List.of(
                        "evaluador" + evaluador1Id + "@unicauca.edu.co",
                        "evaluador" + evaluador2Id + "@unicauca.edu.co"
                ))
                // ⚠️ LÍNEA FALTANTE: Debe tener la fecha y el .build() final.
                .timestamp(LocalDateTime.now())
                .build();
    }
}