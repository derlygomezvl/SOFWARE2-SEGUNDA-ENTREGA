package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.web;

import co.unicauca.comunicacionmicroservicios.domain.ports.in.web.DocumentProcessingWebPort;
import co.unicauca.comunicacionmicroservicios.domain.services.DocumentProcessingService;
import co.unicauca.comunicacionmicroservicios.domain.services.ProjectStateService;
import co.unicauca.comunicacionmicroservicios.domain.services.template.DocumentData;
import co.unicauca.comunicacionmicroservicios.domain.services.template.ProcessResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/documents")
//@Tag(name = "Document Processing", description = "API para procesamiento de documentos usando State y Template Method patterns")
public class DocumentProcessingController implements DocumentProcessingWebPort {

    private final DocumentProcessingService documentProcessingService;
    private final ProjectStateService projectStateService;

    public DocumentProcessingController(DocumentProcessingService documentProcessingService,
                                        ProjectStateService projectStateService) {
        this.documentProcessingService = documentProcessingService;
        this.projectStateService = projectStateService;
    }

    @Override
    public ResponseEntity<ProcessResult> procesarDocumento(
        String proyectoId,
        DocumentProcessingRequest request
    )
    {
        DocumentData documentData = DocumentData.builder()
                .tipo(request.getTipoDocumento())
                .contenido(request.getContenido())
                .usuarioId(request.getUsuarioId())
                .titulo(request.getTitulo())
                .modalidad(request.getModalidad())
                .objetivoGeneral(request.getObjetivoGeneral())
                .objetivosEspecificos(request.getObjetivosEspecificos())
                .archivoAdjunto(request.getArchivoAdjunto())
                .metadata(request.getMetadata())
                .build();

        ProcessResult result = documentProcessingService.procesarDocumento(proyectoId, documentData);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Override
    public ResponseEntity<ProcessResult> procesarFormatoA(
        String proyectoId,
        FormatoARequest request
    )
    {
        DocumentData documentData = DocumentData.createFormatoAData(
                request.getContenido(),
                request.getUsuarioId(),
                request.getTitulo(),
                request.getModalidad(),
                request.getObjetivoGeneral(),
                request.getObjetivosEspecificos(),
                request.getArchivoAdjunto()
        );

        ProcessResult result = documentProcessingService.procesarDocumento(proyectoId, documentData);

        return result.isSuccess() ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @Override
    public ResponseEntity<ProcessResult> procesarAnteproyecto(
        String proyectoId,
        AnteproyectoRequest request
    )
    {
        DocumentData documentData = DocumentData.createAnteproyectoData(
                request.getContenido(),
                request.getUsuarioId(),
                request.getTitulo(),
                request.getArchivoAdjunto()
        );

        ProcessResult result = documentProcessingService.procesarDocumento(proyectoId, documentData);

        return result.isSuccess() ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @Override
    public ResponseEntity<ProcessResult> reenviarFormatoACorregido(
        String proyectoId,
        FormatoACorregidoRequest request
    )
    {
        DocumentData documentData = DocumentData.createFormatoACorregidoData(
                request.getContenido(),
                request.getUsuarioId(),
                request.getObservacionesAnteriores()
        );

        ProcessResult result = documentProcessingService.procesarDocumento(proyectoId, documentData);

        return result.isSuccess() ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }

    @Override
    public ResponseEntity<Map<String, Object>> obtenerPermisos(String proyectoId)
    {
        Map<String, Boolean> permisos = Map.of(
            "puedeAvanzar", projectStateService.puedeAvanzar(proyectoId),
            "permiteReenvioFormatoA", projectStateService.permiteReenvioFormatoA(proyectoId),
            "permiteSubirAnteproyecto", projectStateService.permiteSubirAnteproyecto(proyectoId)
        );

        Map<String, Object> response = Map.of(
            "proyectoId", proyectoId,
            "permisos", permisos,
            "tiposDocumentoSoportados", documentProcessingService.getTiposDocumentoSoportados()
        );

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, String>> obtenerTiposDocumento()
    {
        return ResponseEntity.ok(documentProcessingService.getTiposDocumentoSoportados());
    }

    // DTOs para las requests
    public static class DocumentProcessingRequest {
        private String tipoDocumento;
        private String contenido;
        private String usuarioId;
        private String titulo;
        private String modalidad;
        private String objetivoGeneral;
        private String objetivosEspecificos;
        private String archivoAdjunto;
        private Map<String, Object> metadata;

        // Getters y Setters
        public String getTipoDocumento() { return tipoDocumento; }
        public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }
        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getModalidad() { return modalidad; }
        public void setModalidad(String modalidad) { this.modalidad = modalidad; }
        public String getObjetivoGeneral() { return objetivoGeneral; }
        public void setObjetivoGeneral(String objetivoGeneral) { this.objetivoGeneral = objetivoGeneral; }
        public String getObjetivosEspecificos() { return objetivosEspecificos; }
        public void setObjetivosEspecificos(String objetivosEspecificos) { this.objetivosEspecificos = objetivosEspecificos; }
        public String getArchivoAdjunto() { return archivoAdjunto; }
        public void setArchivoAdjunto(String archivoAdjunto) { this.archivoAdjunto = archivoAdjunto; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    public static class FormatoARequest {
        private String contenido;
        private String usuarioId;
        private String titulo;
        private String modalidad;
        private String objetivoGeneral;
        private String objetivosEspecificos;
        private String archivoAdjunto;

        // Getters y Setters
        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }
        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getModalidad() { return modalidad; }
        public void setModalidad(String modalidad) { this.modalidad = modalidad; }
        public String getObjetivoGeneral() { return objetivoGeneral; }
        public void setObjetivoGeneral(String objetivoGeneral) { this.objetivoGeneral = objetivoGeneral; }
        public String getObjetivosEspecificos() { return objetivosEspecificos; }
        public void setObjetivosEspecificos(String objetivosEspecificos) { this.objetivosEspecificos = objetivosEspecificos; }
        public String getArchivoAdjunto() { return archivoAdjunto; }
        public void setArchivoAdjunto(String archivoAdjunto) { this.archivoAdjunto = archivoAdjunto; }
    }

    public static class AnteproyectoRequest {
        private String contenido;
        private String usuarioId;
        private String titulo;
        private String archivoAdjunto;

        // Getters y Setters
        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }
        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getArchivoAdjunto() { return archivoAdjunto; }
        public void setArchivoAdjunto(String archivoAdjunto) { this.archivoAdjunto = archivoAdjunto; }
    }

    public static class FormatoACorregidoRequest {
        private String contenido;
        private String usuarioId;
        private String observacionesAnteriores;

        // Getters y Setters
        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }
        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
        public String getObservacionesAnteriores() { return observacionesAnteriores; }
        public void setObservacionesAnteriores(String observacionesAnteriores) { this.observacionesAnteriores = observacionesAnteriores; }
    }
}
