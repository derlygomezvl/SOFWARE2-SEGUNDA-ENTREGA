package co.unicauca.comunicacionmicroservicios.service;

import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import co.unicauca.comunicacionmicroservicios.service.template.*;
import co.unicauca.comunicacionmicroservicios.infraestructure.repository.IProyectoGradoRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que orquesta el procesamiento de documentos usando el Template Method Pattern
 */
@Service
public class DocumentProcessingService {

    private final IProyectoGradoRepository proyectoRepository;
    private final Map<String, DocumentProcessingTemplate> processors;

    public DocumentProcessingService(IProyectoGradoRepository proyectoRepository,
                                     FormatoAProcessingTemplate formatoAProcessor,
                                     AnteproyectoProcessingTemplate anteproyectoProcessor,
                                     FormatoACorregidoProcessingTemplate formatoACorregidoProcessor) {
        this.proyectoRepository = proyectoRepository;

        // Registrar todos los procesadores disponibles
        this.processors = new ConcurrentHashMap<>();
        this.processors.put("FORMATO_A", formatoAProcessor);
        this.processors.put("ANTEPROYECTO", anteproyectoProcessor);
        this.processors.put("FORMATO_A_CORREGIDO", formatoACorregidoProcessor);
    }

    public ProcessResult procesarDocumento(String proyectoId, DocumentData documentData) {
        try {
            // Convertir String a Integer
            Integer id = Integer.parseInt(proyectoId);

            // Obtener el proyecto
            ProyectoGrado proyecto = proyectoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado: " + proyectoId));

            // Obtener el procesador adecuado
            DocumentProcessingTemplate processor = processors.get(documentData.getTipo());
            if (processor == null) {
                throw new IllegalArgumentException("Tipo de documento no soportado: " + documentData.getTipo());
            }

            // Ejecutar el procesamiento usando el Template Method
            ProcessResult result = processor.procesarDocumento(proyecto, documentData);

            // Guardar cambios en el proyecto
            if (result.isSuccess()) {
                proyectoRepository.save(proyecto);
            }

            return result;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID de proyecto inválido: " + proyectoId + ". Debe ser un número válido.");
        } catch (RuntimeException e) {
            // Relanzar excepciones de negocio sin modificarlas
            throw e;
        } catch (Exception e) {
            // Capturar cualquier otra excepción y lanzar una más específica
            throw new RuntimeException("Error durante el procesamiento del documento: " + e.getMessage(), e);
        }
    }

    public boolean isTipoDocumentoSoportado(String tipoDocumento) {
        return processors.containsKey(tipoDocumento);
    }

    public Map<String, String> getTiposDocumentoSoportados() {
        return Map.of(
                "FORMATO_A", "Formato A inicial",
                "FORMATO_A_CORREGIDO", "Formato A con correcciones",
                "ANTEPROYECTO", "Anteproyecto completo"
        );
    }
}