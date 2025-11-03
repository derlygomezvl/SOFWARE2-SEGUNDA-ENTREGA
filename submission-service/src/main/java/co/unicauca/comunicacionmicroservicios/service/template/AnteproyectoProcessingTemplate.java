package co.unicauca.comunicacionmicroservicios.service.template;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.domain.enums.ProjectStateEnum;
import co.unicauca.comunicacionmicroservicios.dto.NotificationRequest;
import co.unicauca.comunicacionmicroservicios.dto.NotificationType;
import co.unicauca.comunicacionmicroservicios.dto.Recipient;
import co.unicauca.comunicacionmicroservicios.service.NotificationPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Implementación concreta del Template Method para procesar Anteproyecto
 */
@Component
public class AnteproyectoProcessingTemplate extends DocumentProcessingTemplate {

    public AnteproyectoProcessingTemplate(NotificationPublisher notificationPublisher) {
        super(notificationPublisher);
    }

    @Override
    protected void validarDocumento(DocumentData documentData) {
        logger.info("Validando Anteproyecto...");

        // Validaciones específicas del Anteproyecto
        if (documentData.getContenido() == null || documentData.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del Anteproyecto no puede estar vacío");
        }

        if (documentData.getTitulo() == null || documentData.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del anteproyecto es obligatorio");
        }

        // Validar longitud mínima del anteproyecto
        if (documentData.getContenido().length() < 5000) { // 5000 caracteres mínimo
            throw new IllegalArgumentException("El anteproyecto debe tener al menos 5000 caracteres");
        }

        // Validar estructura básica
        String[] seccionesRequeridas = {"introducción", "objetivos", "metodología", "resultados esperados"};
        for (String seccion : seccionesRequeridas) {
            if (!documentData.getContenido().toLowerCase().contains(seccion)) {
                throw new IllegalArgumentException("El anteproyecto debe contener una sección de: " + seccion);
            }
        }

        logger.info("Anteproyecto validado exitosamente");
    }

    @Override
    protected void validarEstadoProyecto(ProyectoGrado proyecto) {
        logger.info("Validando estado del proyecto para Anteproyecto...");

        // Verificar que el Formato A esté aceptado
        if (proyecto.getEstado() != ProjectStateEnum.FORMATO_A_ACEPTADO) {
            throw new IllegalStateException(
                    "No se puede subir anteproyecto. El Formato A debe estar aceptado. Estado actual: " +
                            proyecto.getEstado().getDescripcion()
            );
        }

        // Verificar que permita subir anteproyecto
        if (!proyecto.permiteSubirAnteproyecto()) {
            throw new IllegalStateException("El proyecto no permite subir anteproyecto en este momento");
        }

        logger.info("Estado del proyecto validado para Anteproyecto");
    }

    @Override
    protected String guardarDocumento(ProyectoGrado proyecto, DocumentData documentData) {
        logger.info("Guardando Anteproyecto en repositorio...");

        // Aquí integrarías con tu IAnteproyectoRepository existente
        String documentId = "ANTEPROYECTO_" + proyecto.getId() + "_" + System.currentTimeMillis();

        // Lógica de guardado específica para Anteproyecto
        logger.info("Anteproyecto guardado con ID: {} para proyecto: {}", documentId, proyecto.getId());

        return documentId;
    }

    @Override
    protected void actualizarEstadoProyecto(ProyectoGrado proyecto) {
        logger.info("Actualizando estado del proyecto después de Anteproyecto...");

        // Usar el State Pattern para manejar el estado
        proyecto.manejarAnteproyecto("Anteproyecto presentado");

        logger.info("Estado actualizado a: {}", proyecto.getEstado().getDescripcion());
    }

    @Override
    protected NotificationRequest construirNotificacion(ProyectoGrado proyecto, DocumentData documentData) {
        logger.info("Construyendo notificación para Anteproyecto...");

        String mensaje = String.format(
                "Se ha presentado un nuevo Anteproyecto para el proyecto: %s\n" +
                        "Título: %s\n" +
                        "Docente: %s\n" +
                        "Por favor asigne evaluadores en el sistema.",
                proyecto.getTitulo(),
                documentData.getTitulo(),
                documentData.getUsuarioId()
        );

        return NotificationRequest.builder()
                .notificationType(NotificationType.ANTEPROYECTO_PRESENTADO)
                .subject("Nuevo Anteproyecto Presentado - " + proyecto.getTitulo())
                .message(mensaje)
                .recipients(List.of(Recipient.builder()
                        .email("jefe.departamento@unicauca.edu.co")
                        .role("Jefe")
                        .name("Jefe")
                        .build()
                ))
                .businessContext(Map.of(
                        "proyectoId", proyecto.getId(),
                        "proyectoTitulo", proyecto.getTitulo(),
                        "docenteId", documentData.getUsuarioId(),
                        "anteproyectoTitulo", documentData.getTitulo()
                ))
                .build();
    }

    @Override
    protected void registrarAuditoriaEspecifica(ProyectoGrado proyecto, DocumentData documentData, String documentId) {
        // Auditoría específica para Anteproyecto
        logger.info("Auditoría específica de Anteproyecto - Proyecto: {}, Longitud: {} caracteres",
                proyecto.getId(), documentData.getContenido().length());
    }

    @Override
    protected void preProcesar(ProyectoGrado proyecto, DocumentData documentData) {
        // Pre-procesamiento específico para Anteproyecto
        logger.info("Pre-procesando Anteproyecto - Analizando estructura...");

        // Podrías analizar la estructura del documento, extraer palabras clave, etc.
    }

    @Override
    protected void postProcesar(ProyectoGrado proyecto, DocumentData documentData, String documentId) {
        // Post-procesamiento específico para Anteproyecto
        logger.info("Post-procesando Anteproyecto - Generando resumen ejecutivo...");

        // Podrías generar un resumen automático, índice, etc.
    }
}