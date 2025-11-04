package com.unicauca.facade_service.controller;

import com.unicauca.facade_service.dto.DocenteDTO;
import com.unicauca.facade_service.dto.FormatoDTO;
import com.unicauca.facade_service.facade.SistemaFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Controlador REST que expone la fachada.
 * @author javiersolanop777
 */
@RestController
@RequestMapping(path = "/api/${api.version}/facade")
public class FacadeController {

    private final Logger ATR_LOGGER = LoggerFactory.getLogger(FacadeController.class);

    @Autowired
    private SistemaFacade atrSistemaFacade;

    public FacadeController()
    {}

    // Microservicio de envios:

    @PostMapping(
            path = "/anteproyectos/subir-anteproyecto",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> subirAnteproyecto(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId,
            Long proyectoId,
            MultipartFile pdf
    ) throws IOException {
        ATR_LOGGER.info("API: POST /facade/anteproyectos");

        String objResponse = atrSistemaFacade.subirAnteproyecto(role, userId, proyectoId, pdf);

        if(objResponse != null)
            return ResponseEntity.ok(objResponse);
        return ResponseEntity.status(502).body(objResponse);
    }

    @GetMapping(path = "/anteproyectos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listarAnteproyectos(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    )
    {
        return ResponseEntity.ok(atrSistemaFacade.listarAnteproyectos(page, size));
    }

    @PatchMapping(path = "/anteproyectos/{id}/estado")
    public ResponseEntity<String> cambiarEstadoAnteproyecto(
            @RequestHeader(value = "X-Service", required = false) String caller,
            @PathVariable Long id,
            String estado,
            String observaciones
    ) {
        ATR_LOGGER.info("API: PATCH /facade/anteproyectos/{versionId}/estado");

        String objResponse = atrSistemaFacade.cambiarEstadoAnteproyecto(caller,id,estado,observaciones);

        if(objResponse != null)
            return ResponseEntity.ok(objResponse);
        return ResponseEntity.status(502).body(objResponse);
    }

    @PostMapping(
            path = "/formatos/subir-formatoA",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> subirFormato(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId,
            FormatoDTO data,
            MultipartFile pdf,
            MultipartFile carta
    ) throws IOException
    {
        ATR_LOGGER.info("API: POST /facade/formatos/subir-formato -> {}", data.getTitulo());
        String objResponse = atrSistemaFacade.subirFormato(role, userId, data, pdf, carta);

        if(objResponse != null)
            return ResponseEntity.ok(objResponse);
        return ResponseEntity.status(502).body(objResponse);
    }

    @GetMapping(path = "/formatos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerFormatoA(@PathVariable Long id)
    {
        return ResponseEntity.ok(atrSistemaFacade.obtenerFormato(id));
    }

    @GetMapping(path = "/formatos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listarFormatoA(
            @RequestParam(name = "docenteId", required = false) String docenteId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    )
    {
        return ResponseEntity.ok(atrSistemaFacade.listarFormato(docenteId, page, size));
    }

    @PostMapping(path = "/formatos/reenviar-formatoA/{proyectoId}")
    public ResponseEntity<String> nuevaVersionFormato(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long proyectoId,
            @RequestPart("pdf") MultipartFile pdf,
            @RequestPart(value = "carta", required = false) MultipartFile carta
    )
    {
        ATR_LOGGER.info("API: POST /facade/formatos/reenviar-formato");
        String objResponse = atrSistemaFacade.reenviarFormato(role, userId, proyectoId, pdf, carta);

        if(objResponse != null)
            return ResponseEntity.ok(objResponse);
        return ResponseEntity.status(502).body(objResponse);
    }

    @PatchMapping(path = "/formatos/{versionId}/estado")
    public ResponseEntity<String> cambiarEstadoFormato(
            @RequestHeader(value = "X-Service", required = false) String caller,
            @PathVariable Long versionId,
            String estado,
            String observaciones,
            Integer evaluadoPor
    ) {
        ATR_LOGGER.info("API: PATCH /facade/formatos/{versionId}/estado");

        String objResponse = atrSistemaFacade.cambiarEstadoFormato(caller,versionId,estado,observaciones,evaluadoPor);

        if(objResponse != null)
            return ResponseEntity.ok(objResponse);
        return ResponseEntity.status(502).body(objResponse);
    }

    // Microservicio de notificaciones:

    @PostMapping(path = "/notifications/sync")
    public ResponseEntity<String> enviarSincrono(
            String prmNotificationType,
            String prmChannel,
            String prmRecipientEmail,
            Map<String, Object> prmBusinessContext
    )
    {
        ATR_LOGGER.info("API: POST /facade/notifications/sync");

        String objResponse = atrSistemaFacade.enviarSincrono(
                prmNotificationType,
                prmChannel,
                prmRecipientEmail,
                prmBusinessContext
        );

        if(objResponse != null)
            return ResponseEntity.ok(objResponse);
        return ResponseEntity.status(502).body("Error al enviar notificacion en servicios externos.");
    }

    @PostMapping(path = "/notifications/async")
    public ResponseEntity<String> enviarAsincrono(
            String prmNotificationType,
            String prmChannel,
            String prmRecipientEmail,
            Map<String, Object> prmBusinessContext
    )
    {
        ATR_LOGGER.info("API: POST /facade/notifications/async");

        String objResponse = atrSistemaFacade.enviarAsincrono(
                prmNotificationType,
                prmChannel,
                prmRecipientEmail,
                prmBusinessContext
        );

        if(objResponse != null)
            return ResponseEntity.ok(objResponse);
        return ResponseEntity.status(502).body("Error al enviar notificacion en servicios externos.");
    }
}
