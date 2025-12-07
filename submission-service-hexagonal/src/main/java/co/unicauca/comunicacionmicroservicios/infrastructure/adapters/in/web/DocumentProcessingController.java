package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.in.web;

import co.unicauca.comunicacionmicroservicios.application.dto.AnteproyectoRequestDTO;
import co.unicauca.comunicacionmicroservicios.application.dto.DocumentProcessingRequestDTO;
import co.unicauca.comunicacionmicroservicios.application.dto.FormatoACorregidoRequestDTO;
import co.unicauca.comunicacionmicroservicios.application.dto.FormatoARequestDTO;
import co.unicauca.comunicacionmicroservicios.domain.ports.in.web.IDocumentProcessingWebPort;
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
public class DocumentProcessingController implements IDocumentProcessingWebPort {

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
        DocumentProcessingRequestDTO request
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
        FormatoARequestDTO request
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
        AnteproyectoRequestDTO request
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
        FormatoACorregidoRequestDTO request
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
}
