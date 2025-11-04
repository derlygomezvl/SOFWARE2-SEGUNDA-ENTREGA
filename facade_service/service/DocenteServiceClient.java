package com.unicauca.facade_service.service;

import com.unicauca.facade_service.dto.DocenteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente REST para docente-service.
 * @author javiersolanop777
 */
@Service
public class DocenteServiceClient {

    private final Logger ATR_LOGGER = LoggerFactory.getLogger(DocenteServiceClient.class);

    private final RestTemplate atrRestTemplate;

    @Autowired
    private RabbitTemplate atrRabbit;

    @Value("${facade.external.docente.base_url}")
    private String ATR_BASE_URL;

    @Value("${facade.external.docente.endpoint}")
    private String ATR_ENDPOINT;

    public DocenteServiceClient(RestTemplate atrRestTemplate)
    {
        this.atrRestTemplate = atrRestTemplate;
    }

    public boolean registrarDocente(DocenteDTO prmDocente)
    {
        String objUrl = ATR_BASE_URL + ATR_ENDPOINT;

        try {
            HttpHeaders objHeaders = new HttpHeaders();
            objHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<DocenteDTO> objRequest = new HttpEntity<>(prmDocente, objHeaders);

            ResponseEntity<String> objResponse = atrRestTemplate.postForEntity(objUrl, objRequest, String.class);
            return objResponse.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            ATR_LOGGER.error("Error registrando docente en {} : {}", objUrl, e.getMessage());
            return false;
        }
    }
}
