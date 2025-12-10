package co.unicauca.comunicacionmicroservicios.domain.services.template;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.application.dto.NotificationRequest;
import co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.events.NotificationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TEMPLATE METHOD PATTERN
 * Clase abstracta que define el esqueleto del proceso de documentos
 * Las subclases implementan los pasos específicos para cada tipo de documento
 */
public abstract class DocumentProcessingTemplate {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final NotificationPublisher notificationPublisher;

    protected DocumentProcessingTemplate(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * TEMPLATE METHOD - Define el esqueleto del proceso que no puede ser modificado
     * Este es el método principal que orquesta todo el flujo
     */
    public final ProcessResult procesarDocumento(ProyectoGrado proyecto, DocumentData documentData) {
        logger.info("Iniciando procesamiento de documento para proyecto: {}", proyecto.getId());

        try {
            // 1. VALIDAR DOCUMENTO
            logger.info("Paso 1: Validando documento...");
            validarDocumento(documentData);

            // 2. VALIDAR ESTADO DEL PROYECTO
            logger.info("Paso 2: Validando estado del proyecto...");
            validarEstadoProyecto(proyecto);

            // 3. GUARDAR DOCUMENTO
            logger.info("Paso 3: Guardando documento...");
            String documentId = guardarDocumento(proyecto, documentData);

            // 4. ACTUALIZAR ESTADO DEL PROYECTO
            logger.info("Paso 4: Actualizando estado del proyecto...");
            actualizarEstadoProyecto(proyecto);

            // 5. NOTIFICAR INTERESADOS
            logger.info("Paso 5: Notificando interesados...");
            notificarInteresados(proyecto, documentData);

            // 6. REGISTRAR AUDITORÍA
            logger.info("Paso 6: Registrando auditoría...");
            registrarAuditoria(proyecto, documentData, documentId);

            logger.info("Procesamiento completado exitosamente para proyecto: {}", proyecto.getId());

            return ProcessResult.builder()
                    .success(true)
                    .message("Documento procesado exitosamente")
                    .documentId(documentId)
                    .proyectoId(String.valueOf(proyecto.getId()))
                    .estadoActual(proyecto.getEstado().getDescripcion())
                    .build();

        } catch (Exception e) {
            logger.error("Error en el procesamiento del documento para proyecto: {}", proyecto.getId(), e);

            // 7. MANEJAR ERROR (método hook)
            manejarError(proyecto, documentData, e);

            return ProcessResult.builder()
                    .success(false)
                    .message("Error al procesar documento: " + e.getMessage())
                    .proyectoId(String.valueOf(proyecto.getId()))
                    .build();
        }
    }

    // ========== MÉTODOS ABSTRACTOS (deben ser implementados por las subclases) ==========

    /**
     * Validar el contenido específico del documento
     */
    protected abstract void validarDocumento(DocumentData documentData);

    /**
     * Validar que el proyecto esté en estado adecuado para este documento
     */
    protected abstract void validarEstadoProyecto(ProyectoGrado proyecto);

    /**
     * Guardar el documento en el repositorio específico
     * @return ID del documento guardado
     */
    protected abstract String guardarDocumento(ProyectoGrado proyecto, DocumentData documentData);

    /**
     * Actualizar el estado del proyecto según el documento procesado
     */
    protected abstract void actualizarEstadoProyecto(ProyectoGrado proyecto);

    /**
     * Construir la notificación específica para este tipo de documento
     */
    protected abstract NotificationRequest construirNotificacion(ProyectoGrado proyecto, DocumentData documentData);

    // ========== MÉTODOS CONCRETOS (compartidos por todas las subclases) ==========

    /**
     * Notificar a los interesados - Comportamiento común
     */
    private void notificarInteresados(ProyectoGrado proyecto, DocumentData documentData) {
        NotificationRequest notificacion = construirNotificacion(proyecto, documentData);
        notificationPublisher.publishNotification(notificacion, "Evento de procesamiento de documento");

    }

    /**
     * Registrar auditoría - Comportamiento común con hook opcional
     */
    private void registrarAuditoria(ProyectoGrado proyecto, DocumentData documentData, String documentId) {
        logger.info("Auditoría registrada - Proyecto: {}, Documento: {}, Tipo: {}, Usuario: {}",
                proyecto.getId(), documentId, documentData.getTipo(), documentData.getUsuarioId());

        // Hook para auditoría específica
        registrarAuditoriaEspecifica(proyecto, documentData, documentId);
    }

    // ========== MÉTODOS HOOK (opcionales para las subclases) ==========

    /**
     * Hook para auditoría específica - Las subclases pueden override este método
     */
    protected void registrarAuditoriaEspecifica(ProyectoGrado proyecto, DocumentData documentData, String documentId) {
        // Implementación por defecto vacía
    }

    /**
     * Hook para manejo de errores específico - Las subclases pueden override este método
     */
    protected void manejarError(ProyectoGrado proyecto, DocumentData documentData, Exception error) {
        // Implementación por defecto - solo loguear
        logger.error("Error manejado por implementación por defecto", error);
    }

    /**
     * Hook para pre-procesamiento - Las subclases pueden override este método
     */
    protected void preProcesar(ProyectoGrado proyecto, DocumentData documentData) {
        // Implementación por defecto vacía
    }

    /**
     * Hook para post-procesamiento - Las subclases pueden override este método
     */
    protected void postProcesar(ProyectoGrado proyecto, DocumentData documentData, String documentId) {
        // Implementación por defecto vacía
    }
}