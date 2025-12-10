package co.unicauca.review.client;

import co.unicauca.review.dto.response.FormatoAReviewDTO;
import co.unicauca.review.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.unicauca.review.enums.Decision;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class SubmissionServiceClient {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceClient.class);

    private final WebClient webClient;
    private final WebClient keycloakWebClient; // WebClient para obtener tokens

    // Propiedades inyectadas
    private final String tokenUri;
    private final String clientId;
    private final String clientSecret;

    // Constructor modificado para inyectar valores y crear un WebClient para Keycloak
    public SubmissionServiceClient(
            @Qualifier("submissionWebClient") WebClient submissionWebClient,
            @Value("${keycloak.m2m.token-uri}") String tokenUri,
            @Value("${keycloak.m2m.client-id}") String clientId,
            @Value("${keycloak.m2m.client-secret}") String clientSecret
    ) {
        this.webClient = submissionWebClient;
        this.tokenUri = tokenUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        // Creamos un WebClient genérico para Keycloak
        this.keycloakWebClient = WebClient.builder().build();
    }

    public FormatoADTO getFormatoA(Long formatoAId) {
        log.debug("Obteniendo Formato A con id: {}", formatoAId);

        try {
            return webClient.get()
                    .uri("/api/submissions/formatoA/{id}", formatoAId)
                    .retrieve()
                    .bodyToMono(FormatoADTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Error obteniendo Formato A {}: {}", formatoAId, e.getMessage());
            throw new ResourceNotFoundException("Formato A no encontrado: " + formatoAId);
        }
    }

    // Método para obtener el token de servicio (Client Credentials Flow)
    private String getServiceToken() {
        // Body para la petición de token (M2M)
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", this.clientId);
        formData.add("client_secret", this.clientSecret);

        try {
            String responseBody = keycloakWebClient.post()
                    .uri(this.tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Usamos ObjectMapper o JSONObject (como en tu ejemplo) para parsear el token
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> tokenResponse = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

            return (String) tokenResponse.get("access_token");

        } catch (Exception e) {
            log.error("Error obteniendo token de Keycloak M2M: {}", e.getMessage());
            // Si el servicio no puede obtener el token, la comunicación falla.
            throw new RuntimeException("Fallo en la autenticación M2M con Keycloak.", e);
        }
    }

    // Método Modificado: Ahora usa el token
    public AnteproyectoDTO getAnteproyecto(Long anteproyectoId) {
        log.debug("Obteniendo Anteproyecto con id: {}", anteproyectoId);

        // 1. Obtener el Token M2M
        String token = getServiceToken(); // Obtenemos el token de servicio

        try {
            return webClient.get()
                    .uri("/api/submissions/anteproyecto/{id}", anteproyectoId)
                    // 2. AÑADIR EL HEADER DE AUTORIZACIÓN
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(AnteproyectoDTO.class)
                    .block();
        } catch (Exception e) {
            // Si falla aquí, la razón más probable es el 401/404, pero ya intentamos el JWT.
            log.error("Error obteniendo Anteproyecto {}. Posiblemente 404 real o token inválido: {}", anteproyectoId, e.getMessage());
            throw new ResourceNotFoundException("Anteproyecto no encontrado: " + anteproyectoId);
        }
    }

    public Page<FormatoAReviewDTO> getFormatosAPendientes(int page, int size) {
        log.debug("Obteniendo Formatos A pendientes - page: {}, size: {}", page, size);

        try {
            // Simular respuesta paginada
            List<FormatoAReviewDTO> content = List.of(
                new FormatoAReviewDTO(
                    1L,
                    "Implementación de IA en agricultura",
                    "Dr. Juan Pérez",
                    "juan.perez@unicauca.edu.co",
                    List.of("estudiante1@unicauca.edu.co", "estudiante2@unicauca.edu.co"),
                    LocalDateTime.now().minusDays(2),
                    "EN_REVISION"
                )
            );
            return new PageImpl<>(content);
        } catch (Exception e) {
            log.error("Error obteniendo Formatos A pendientes: {}", e.getMessage());
            throw new RuntimeException("Error al obtener Formatos A pendientes", e);
        }
    }

    public void updateFormatoAEstado(Long formatoAId, Map<String, String> body) {
        log.debug("Actualizando estado de Formato A {}: {}", formatoAId, body);

        try {
            webClient.patch()
                    .uri("/api/submissions/formatoA/{id}/estado", formatoAId)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Estado de Formato A {} actualizado exitosamente", formatoAId);
        } catch (Exception e) {
            log.error("Error actualizando estado de Formato A {}: {}", formatoAId, e.getMessage());
            throw new RuntimeException("Error al actualizar estado de Formato A", e);
        }
    }

    public void updateAnteproyectoEstado(Long anteproyectoId, Map<String, String> body) {
        log.debug("Actualizando estado de Anteproyecto {}: {}", anteproyectoId, body);

        try {
            webClient.patch()
                    .uri("/api/submissions/anteproyecto/{id}/estado", anteproyectoId)
                    .header("X-Service", "review") // CAMBIO CLAVE: Reemplazado "REVIEW_SERVICE" por "review"
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Estado de Anteproyecto {} actualizado exitosamente", anteproyectoId);
        } catch (Exception e) {
            log.error("Error actualizando estado de Anteproyecto {}: {}", anteproyectoId, e.getMessage());
            throw new RuntimeException("Error al actualizar estado de Anteproyecto", e);
        }
    }

    // DTOs internos para comunicación
    public static class FormatoADTO {
        private Long id;
        private String titulo;
        private String estado;
        private String docenteDirectorNombre;
        private String docenteDirectorEmail;
        private List<String> estudiantesEmails;

        public FormatoADTO() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public String getDocenteDirectorNombre() { return docenteDirectorNombre; }
        public void setDocenteDirectorNombre(String nombre) { this.docenteDirectorNombre = nombre; }

        public String getDocenteDirectorEmail() { return docenteDirectorEmail; }
        public void setDocenteDirectorEmail(String email) { this.docenteDirectorEmail = email; }

        public List<String> getEstudiantesEmails() { return estudiantesEmails; }
        public void setEstudiantesEmails(List<String> emails) { this.estudiantesEmails = emails; }
    }

    public static class AnteproyectoDTO {
        private Long id;
        private String titulo;
        private String estado;
        private String docenteDirectorNombre;
        private String docenteDirectorEmail;
        private List<String> estudiantesEmails;

        public AnteproyectoDTO() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public String getDocenteDirectorNombre() { return docenteDirectorNombre; }
        public void setDocenteDirectorNombre(String nombre) { this.docenteDirectorNombre = nombre; }

        public String getDocenteDirectorEmail() { return docenteDirectorEmail; }
        public void setDocenteDirectorEmail(String email) { this.docenteDirectorEmail = email; }

        public List<String> getEstudiantesEmails() { return estudiantesEmails; }
        public void setEstudiantesEmails(List<String> emails) { this.estudiantesEmails = emails; }
    }
    // Nuevo método para implementar la llamada desde AsignacionService
    public void cambiarEstadoAnteproyecto(Long anteproyectoId, Decision finalDecision) {
        log.debug("Cambiando estado de Anteproyecto {} a decisión final: {}", anteproyectoId, finalDecision);

        // Creamos el cuerpo de la solicitud en el formato que ya usa updateAnteproyectoEstado
        // Asumimos que Submission Service espera {"estado": "APROBADO" o "RECHAZADO"}
        Map<String, String> body = Map.of("estado", finalDecision.name());

        // Reutilizamos el método existente para ejecutar la lógica de WebClient
        updateAnteproyectoEstado(anteproyectoId, body);

        log.info("Estado de Anteproyecto {} actualizado exitosamente a: {}", anteproyectoId, finalDecision);
    }
}

