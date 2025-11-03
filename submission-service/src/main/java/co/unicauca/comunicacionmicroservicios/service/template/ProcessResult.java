package co.unicauca.comunicacionmicroservicios.service.template;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessResult {
    private boolean success;
    private String message;
    private String documentId;
    private String proyectoId;
    private String estadoActual;
    private String siguientePaso;

    public static ProcessResult success(String documentId, String proyectoId, String estadoActual) {
        return ProcessResult.builder()
                .success(true)
                .message("Procesamiento completado exitosamente")
                .documentId(documentId)
                .proyectoId(proyectoId)
                .estadoActual(estadoActual)
                .siguientePaso(determinarSiguientePaso(estadoActual))
                .build();
    }

    public static ProcessResult error(String message, String proyectoId) {
        return ProcessResult.builder()
                .success(false)
                .message(message)
                .proyectoId(proyectoId)
                .build();
    }

    private static String determinarSiguientePaso(String estadoActual) {
        switch (estadoActual) {
            case "Formato A Presentado":
                return "Esperar evaluación del coordinador";
            case "Formato A Aceptado":
                return "Subir anteproyecto";
            case "Anteproyecto Presentado":
                return "Esperar asignación de evaluadores";
            case "Anteproyecto Aceptado":
                return "Continuar con desarrollo del proyecto";
            default:
                return "Consultar con coordinador";
        }
    }
}