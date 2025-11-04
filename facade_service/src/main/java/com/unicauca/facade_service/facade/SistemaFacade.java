package com.unicauca.facade_service.facade;

import com.unicauca.facade_service.dto.DocenteDTO;
import com.unicauca.facade_service.dto.FormatoDTO;
import com.unicauca.facade_service.dto.NotificacionDTO;
import com.unicauca.facade_service.service.DocenteServiceClient;
import com.unicauca.facade_service.service.SubmissionServiceClient;
import com.unicauca.facade_service.service.NotificacionServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Fachada principal que orquesta las llamadas a servicios externos.
 * @author javiersolanop777
 */
@Component
public class SistemaFacade {

    private final Logger atrLogger = LoggerFactory.getLogger(SistemaFacade.class);

    private final DocenteServiceClient atrDocenteServiceClient;
    private final SubmissionServiceClient atrSubmissionService;
    private final NotificacionServiceClient atrNotificacionServiceClient;

    public SistemaFacade(
            DocenteServiceClient atrDocenteServiceClient,
             SubmissionServiceClient atrSubmissionService,
             NotificacionServiceClient atrNotificacionServiceClient
    )
    {
        this.atrDocenteServiceClient = atrDocenteServiceClient;
        this.atrSubmissionService = atrSubmissionService;
        this.atrNotificacionServiceClient = atrNotificacionServiceClient;
    }

    public boolean registrarDocente(DocenteDTO prmDocente) {

        atrLogger.info("Facade: iniciar registro docente -> {}", prmDocente.getAtrNombre());

        boolean objRegistroOk = atrDocenteServiceClient.registrarDocente(prmDocente);

        if (!objRegistroOk) {
            atrLogger.warn("Facade: registro docente falló para -> {}", prmDocente.getAtrNombre());
            return false;
        }

        NotificacionDTO objNotificacion = new NotificacionDTO();
        objNotificacion.setAtrAsunto("Docente registrado");
        objNotificacion.setAtrMensaje("Se registró el docente: " + prmDocente.getAtrNombre());

        return true;
    }

    public String subirAnteproyecto(
            String prmRole,
            String prmUserId,
            Long prmProyectoId,
            MultipartFile prmPdf
    ) throws IOException {
        atrLogger.info("Facade: iniciar subir anteproyecto -> {}", prmProyectoId);

        String objSubidaOk = atrSubmissionService.subirAnteproyecto(
                prmRole,
                prmUserId,
                prmProyectoId,
                prmPdf
        );

        if (objSubidaOk == null) {
            atrLogger.warn("Facade: subida de anteproyecto falló -> {}", prmProyectoId);
        }
        return objSubidaOk;
    }

    public String listarAnteproyectos(int prmPage, int prmSize)
    {
        return atrSubmissionService.listarAnteproyectos(prmPage, prmSize);
    }

    public String cambiarEstadoAnteproyecto(
            String prmCaller,
            Long prmId,
            String prmEstado,
            String prmObservaciones
    )
    {
        atrLogger.info("Facade: Cambiar estado anteproyecto");

        String objSubidaOk = atrSubmissionService.cambiarEstadoAnteproyecto(
                prmCaller,
                prmId,
                prmEstado,
                prmObservaciones
        );

        if (objSubidaOk == null) {
            atrLogger.warn("Facade: cambio de estado de anteproyecto falló");
        }
        return objSubidaOk;
    }

    public String subirFormato(
            String prmRole,
            String prmUserId,
            FormatoDTO prmFormato,
            MultipartFile prmPdf,
            MultipartFile prmCarta
    ) throws IOException
    {
        atrLogger.info("Facade: iniciar subir formato -> {}", prmFormato.getTitulo());

        String objSubidaOk = atrSubmissionService.subirFormato(
                prmRole,
                prmUserId,
                prmFormato,
                prmPdf,
                prmCarta
        );

        if (objSubidaOk == null) {
            atrLogger.warn("Facade: subida de formato falló -> {}", prmFormato.getTitulo());
        }
        return objSubidaOk;
    }

    public String obtenerFormato(Long prmId)
    {
        atrLogger.info("Facade: obtener formato con id -> {}", prmId);
        return atrSubmissionService.obtenerFormato(prmId);
    }

    public String listarFormato(String prmDocenteId, int prmPage, int prmSize)
    {
        atrLogger.info("Facade: obtener formatos con docente id -> {}", prmDocenteId);
        return atrSubmissionService.listarFormato(prmDocenteId, prmPage, prmSize);
    }

    public String reenviarFormato(
            String prmRole,
            String prmUserId,
            Long prmProyectoId,
            MultipartFile prmPdf,
            MultipartFile prmCarta
    ) {
        atrLogger.info("Facade: iniciar subir formato con proyecto id -> {}", prmProyectoId);

        String objSubidaOk = atrSubmissionService.reenviarFormato(
                prmRole,
                prmUserId,
                prmProyectoId,
                prmPdf,
                prmCarta
        );

        if (objSubidaOk == null) {
            atrLogger.warn("Facade: subida de formato falló con proyecto id -> {}", prmProyectoId);
        }
        return objSubidaOk;
    }

    public String cambiarEstadoFormato(
            String prmCaller,
            Long prmVersionId,
            String prmEstado,
            String prmObservaciones,
            Integer prmEvaluadoPor
    )
    {
        atrLogger.info("Facade: Cambiar estado formato");

        String objSubidaOk = atrSubmissionService.cambiarEstadoFormato(
                prmCaller,
                prmVersionId,
                prmEstado,
                prmObservaciones,
                prmEvaluadoPor
        );

        if (objSubidaOk == null) {
            atrLogger.warn("Facade: cambio de estado de formato falló");
        }
        return objSubidaOk;
    }

    public String enviarSincrono(
            String prmNotificationType,
            String prmChannel,
            String prmRecipientEmail,
            Map<String, Object> prmBusinessContext
    )
    {
        atrLogger.info("Facade: enviar notificacion sincrona");

        String objSubidaOk = atrNotificacionServiceClient.enviarSincrono(
                prmNotificationType,
                prmChannel,
                prmRecipientEmail,
                prmBusinessContext
        );

        if(objSubidaOk == null) {
            atrLogger.warn("Facade: enviar notificacion sincrona falló");
        }
        return objSubidaOk;
    }

    public String enviarAsincrono(
            String prmNotificationType,
            String prmChannel,
            String prmRecipientEmail,
            Map<String, Object> prmBusinessContext
    )
    {
        atrLogger.info("Facade: enviar notificacion asincrona");

        String objSubidaOk = atrNotificacionServiceClient.enviarAsincrono(
                prmNotificationType,
                prmChannel,
                prmRecipientEmail,
                prmBusinessContext
        );

        if(objSubidaOk == null) {
            atrLogger.warn("Facade: enviar notificacion asincrona falló");
        }
        return objSubidaOk;
    }

    public String procesarDocumento(
            String prmProyectoId,
            String prmTipoDocumento,
            String prmContenido,
            String prmUsuarioId,
            String prmTitulo,
            String prmModalidad,
            String prmObjetivoGeneral,
            String prmObjetivosEspecificos,
            String prmArchivoAdjunto,
            Map<String, Object> prmMetaData
    )
    {
        atrLogger.info("Facade: procesar documento");

        String objSubidaOk = atrSubmissionService.procesarDocumento(
                prmProyectoId,
                prmTipoDocumento,
                prmContenido,
                prmUsuarioId,
                prmTitulo,
                prmModalidad,
                prmObjetivoGeneral,
                prmObjetivosEspecificos,
                prmArchivoAdjunto,
                prmMetaData
        );

        if(objSubidaOk == null) {
            atrLogger.warn("Facade: procesar documento falló");
        }
        return objSubidaOk;
    }

    public String procesarFormatoA(
            String prmProyectoId,
            String prmContenido,
            String prmUsuarioId,
            String prmTitulo,
            String prmModalidad,
            String prmObjetivoGeneral,
            String prmObjetivosEspecificos,
            String prmArchivoAdjunto
    )
    {
        atrLogger.info("Facade: procesar formato A");

        String objSubidaOk = atrSubmissionService.procesarFormatoA(
                prmProyectoId,
                prmContenido,
                prmUsuarioId,
                prmTitulo,
                prmModalidad,
                prmObjetivoGeneral,
                prmObjetivosEspecificos,
                prmArchivoAdjunto
        );

        if(objSubidaOk == null) {
            atrLogger.warn("Facade: procesar formato A falló");
        }
        return objSubidaOk;
    }

    public String procesarAnteproyecto(
            String prmProyectoId,
            String prmContenido,
            String prmUsuarioId,
            String prmTitulo,
            String prmArchivoAdjunto
    )
    {
        atrLogger.info("Facade: procesar anteproyecto");

        String objSubidaOk = atrSubmissionService.procesarAnteproyecto(
                prmProyectoId,
                prmContenido,
                prmUsuarioId,
                prmTitulo,
                prmArchivoAdjunto
        );

        if(objSubidaOk == null) {
            atrLogger.warn("Facade: procesar anteproyecto falló");
        }
        return objSubidaOk;
    }

    public String reenviarFormatoACorregido(
            String prmProyectoId,
            String prmContenido,
            String prmUsuarioId,
            String prmObsrvacionesAnteriores
    )
    {
        atrLogger.info("Facade: reenviar formato A corregido");

        String objSubidaOk = atrSubmissionService.reenviarFormatoACorregido(
                prmProyectoId,
                prmContenido,
                prmUsuarioId,
                prmObsrvacionesAnteriores
        );

        if(objSubidaOk == null) {
            atrLogger.warn("Facade: reenviar formato A corregido falló");
        }
        return objSubidaOk;
    }

    public String obtenerPermisos(String prmProyectoId)
    {
        atrLogger.info("Facade: obtener permisos");
        return atrSubmissionService.obtenerPermisos(prmProyectoId);
    }

    public String obtenerTiposDocumentos()
    {
        atrLogger.info("Facade: obtener tipos de documentos");
        return atrSubmissionService.obtenerTiposDocumentos();
    }
}
