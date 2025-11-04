package com.unicauca.facade_service.service;

import com.unicauca.facade_service.dto.FormatoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

/**
 * Cliente REST para submission-service.
 * @author javiersolanop777
 */
@Service
public class SubmissionServiceClient {

    private final Logger ATR_LOGGER = LoggerFactory.getLogger(SubmissionServiceClient.class);

    private final RestTemplate atrRestTemplate;

    @Value("${facade.external.submission_service.base_url}")
    private String atrBaseUrl;

    @Value("${facade.external.submission_service.formatos.endpoint}")
    private String atrFormatoEndpoint;

    @Value("${facade.external.submission_service.anteproyectos.endpoint}")
    private String atrAnteproyectoEndpoint;

    @Value("${facade.external.submission_service.documents.endpoint}")
    private String atrDocumentoEndpoint;

    public SubmissionServiceClient(
            RestTemplate atrRestTemplate
    )
    {
        this.atrRestTemplate = atrRestTemplate;
    }

    public String subirAnteproyecto(
            String prmRole,
            String prmUserId,
            Long prmProyectoId,
            MultipartFile prmPdf
    ) throws IOException
    {
        String objUrl = atrBaseUrl + atrAnteproyectoEndpoint;

        try
        {
            ByteArrayResource pdfResource = new ByteArrayResource(prmPdf.getBytes()) {
                @Override
                public String getFilename() {
                    return prmPdf.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("proyectoId", prmProyectoId);
            body.add("pdf", pdfResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("X-User-Role", prmRole);
            headers.add("X-User-Id", prmUserId);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = atrRestTemplate.exchange(
                    objUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return response.getBody();

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    public String listarAnteproyectos(int prmPage, int prmSize)
    {
        String objUrl =  atrBaseUrl + atrAnteproyectoEndpoint + "?page=" + prmPage + "&size=" + prmSize;

        try {
            ResponseEntity<String> objResponse = atrRestTemplate.getForEntity(objUrl, String.class);
            return objResponse.getBody();
        } catch (RestClientException e) {
            ATR_LOGGER.error("Error en listar anteproyectos: {}", e.getMessage());
            return null;
        }
    }

    public String cambiarEstadoAnteproyecto(
            String prmCaller,
            Long prmId,
            String prmEstado,
            String prmObservaciones
    )
    {
        String objUrl = atrBaseUrl + atrAnteproyectoEndpoint + "/" + prmId + "/estado";

        try {
            HttpHeaders objHeaders = new HttpHeaders();
            objHeaders.add("X-Service", prmCaller);

            MultiValueMap<String, Object> objBody = new LinkedMultiValueMap<>();
            objBody.add("estado", prmEstado);
            objBody.add("observaciones", prmObservaciones);
            HttpEntity<MultiValueMap<String, Object>> objRequest = new HttpEntity<>(objBody, objHeaders);

            ResponseEntity<String> objResponse = atrRestTemplate.exchange(objUrl, HttpMethod.PATCH, objRequest, String.class);
            return objResponse.getBody();

        } catch (Exception ex) {
            ATR_LOGGER.error("Error en cambiar estado anteproyecto: {}", ex.getMessage());
            return ex.getMessage();
        }
    }

    public String subirFormato(
            String prmRole,
            String prmUserId,
            FormatoDTO prmFormato,
            MultipartFile prmPdf,
            MultipartFile prmCarta
    ) throws IOException
    {
        String objUrl = atrBaseUrl + atrFormatoEndpoint;

        try
        {
            ByteArrayResource pdfResource = new ByteArrayResource(prmPdf.getBytes()) {
                @Override
                public String getFilename() {
                    return prmPdf.getOriginalFilename();
                }
            };

            ByteArrayResource cartaResource = new ByteArrayResource(prmCarta.getBytes()) {
                @Override
                public String getFilename() {
                    return prmCarta.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = prmFormato.toMultiValueMap();
            body.add("pdf", pdfResource);
            body.add("carta", cartaResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("X-User-Role", prmRole);
            headers.add("X-User-Id", prmUserId);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = atrRestTemplate.exchange(
                    objUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return response.getBody();

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    public String obtenerFormato(Long prmId)
    {
        String objUrl = atrBaseUrl + atrFormatoEndpoint + "/" + prmId;

        try {
            ResponseEntity<String> objResponse = atrRestTemplate.getForEntity(objUrl, String.class);
            return objResponse.getBody();
        } catch (RestClientException e) {
            ATR_LOGGER.error("Error en obtener formato: {}", e.getMessage());
            return null;
        }
    }

    public String listarFormato(String prmDocenteId, int prmPage, int prmSize)
    {
        String objUrl = atrBaseUrl + atrFormatoEndpoint + "?docenteId=" + prmDocenteId + "&page=" + prmPage + "&size=" + prmSize;

        try {
            ResponseEntity<String> objResponse = atrRestTemplate.getForEntity(objUrl, String.class);
            return objResponse.getBody();
        } catch (RestClientException e) {
            ATR_LOGGER.error("Error en listar formatos: {}", e.getMessage());
            return null;
        }
    }

    public String reenviarFormato(
            String prmRole,
            String prmUserId,
            Long prmProyectoId,
            MultipartFile prmPdf,
            MultipartFile prmCarta
    )
    {
        String objUrl = atrBaseUrl + atrFormatoEndpoint + "/" + prmProyectoId + "/nueva-version";

        try
        {
            ByteArrayResource pdfResource = new ByteArrayResource(prmPdf.getBytes()) {
                @Override
                public String getFilename() {
                    return prmPdf.getOriginalFilename();
                }
            };

            ByteArrayResource cartaResource = new ByteArrayResource(prmCarta.getBytes()) {
                @Override
                public String getFilename() {
                    return prmCarta.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("pdf", pdfResource);
            body.add("carta", cartaResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("X-User-Role", prmRole);
            headers.add("X-User-Id", prmUserId);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = atrRestTemplate.exchange(
                    objUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return response.getBody();

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    public String cambiarEstadoFormato(
            String prmCaller,
            Long prmVersionId,
            String prmEstado,
            String prmObservaciones,
            Integer prmEvaluadoPor
    )
    {
        String objUrl = atrBaseUrl + atrFormatoEndpoint + "/" + prmVersionId + "/estado";

        try {
            HttpHeaders objHeaders = new HttpHeaders();
            objHeaders.add("X-Service", prmCaller);

            MultiValueMap<String, Object> objBody = new LinkedMultiValueMap<>();
            objBody.add("estado", prmEstado);
            objBody.add("observaciones", prmObservaciones);
            objBody.add("evaluadoPor", prmEvaluadoPor);
            HttpEntity<MultiValueMap<String, Object>> objRequest = new HttpEntity<>(objBody, objHeaders);

            ResponseEntity<String> objResponse = atrRestTemplate.exchange(objUrl, HttpMethod.PATCH, objRequest, String.class);
            return objResponse.getBody();

        } catch (Exception ex) {
            ATR_LOGGER.error("Error en cambiar estado formato: {}", ex.getMessage());
            return ex.getMessage();
        }
    }

    public String procesarDocumento(
            String prmProyectoId,
            String prmTipoDocumento,
            String prmUsuarioId,
            String prmTitulo,
            String prmModalidad,
            String prmObjetivoGeneral,
            String prmObjetivosEspecificos,
            String prmArchivoAdjunto,
            Map<String, Object> prmMetaData
    )
    {
//        String objUrl = atrBaseUrl + atrDocumentoEndpoint + "/proyecto/" + prmProyectoId + "/procesar";
//
//        try
//            {
//                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//
//                body.add("proyectoId", prmProyectoId);
//                body.add("pdf", pdfResource);
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//                headers.add("X-User-Role", prmRole);
//                headers.add("X-User-Id", prmUserId);
//
//                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//                ResponseEntity<String> response = atrRestTemplate.exchange(
//                        objUrl,
//                        HttpMethod.POST,
//                        requestEntity,
//                        String.class
//                );
//
//                return response.getBody();
//
//        } catch (Exception ex) {
//                ex.printStackTrace();
//                return ex.getMessage();
//        }
        return null;
    }
}
