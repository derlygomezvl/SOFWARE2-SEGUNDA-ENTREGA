package com.unicauca.facade_service.service;

import com.unicauca.facade_service.dto.NotificacionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Cliente REST para notificacion-service.
 * @author javiersolanop777
 */
@Service
public class NotificacionServiceClient {

    private final Logger ATR_LOGGER = LoggerFactory.getLogger(NotificacionServiceClient.class);

    @Autowired
    private RestTemplate atrRestTemplate;

    @Value("${facade.external.notificacion.base_url}")
    private String atrBaseUrl;

    @Value("${facade.external.notificacion.endpoint}")
    private String atrEndpoint;

    public NotificacionServiceClient()
    {}

    public String enviarSincrono(
            String prmNotificationType,
            String prmChannel,
            String prmRecipientEmail,
            Map<String, Object> prmBusinessContext
    )
    {
        String objUrl = atrBaseUrl + atrEndpoint;

        try {
            HttpHeaders objHeaders = new HttpHeaders();
            objHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> objBody = new HashMap<>();
            objBody.put("notificationType", prmNotificationType);
            objBody.put("channel", prmChannel);
            objBody.put("recipientEmail", prmRecipientEmail);
            objBody.put("businessContext", prmBusinessContext);

            HttpEntity<Map<String, Object>> objRequest = new HttpEntity<>(objBody, objHeaders);

            ResponseEntity<String> objResponse = atrRestTemplate.postForEntity(objUrl, objRequest, String.class);

            ATR_LOGGER.info("NotificacionSincrona -> URL: {}, status: {}", objUrl, objResponse.getStatusCodeValue());
            return objResponse.getBody();

        } catch (RestClientException e) {
            ATR_LOGGER.error("Error al enviar notificacion a {} : {}", objUrl, e.getMessage());
            return null;
        }
    }

    public String enviarAsincrono(
            String prmNotificationType,
            String prmChannel,
            String prmRecipientEmail,
            Map<String, Object> prmBusinessContext
    )
    {
        String objUrl = atrBaseUrl + atrEndpoint + "/async";

        try {
            HttpHeaders objHeaders = new HttpHeaders();
            objHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> objBody = new HashMap<>();
            objBody.put("notificationType", prmNotificationType);
            objBody.put("channel", prmChannel);
            objBody.put("recipientEmail", prmRecipientEmail);
            objBody.put("businessContext", prmBusinessContext);

            HttpEntity<Map<String, Object>> objRequest = new HttpEntity<>(objBody, objHeaders);

            ResponseEntity<String> objResponse = atrRestTemplate.postForEntity(objUrl, objRequest, String.class);

            ATR_LOGGER.info("NotificacionSincrona -> URL: {}, status: {}", objUrl, objResponse.getStatusCodeValue());
            return objResponse.getBody();

        } catch (RestClientException e) {
            ATR_LOGGER.error("Error al enviar notificacion a {} : {}", objUrl, e.getMessage());
            return null;
        }
    }
}
