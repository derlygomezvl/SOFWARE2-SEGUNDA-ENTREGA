package co.unicauca.comunicacionmicroservicios.service.template;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DocumentData {
    private String tipo; // "FORMATO_A", "ANTEPROYECTO", "FORMATO_A_CORREGIDO"
    private String contenido;
    private String usuarioId;
    private String titulo;
    private String modalidad;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private String archivoAdjunto; // Base64 o URL
    private Map<String, Object> metadata;

    public static DocumentData createFormatoAData(String contenido, String usuarioId, String titulo,
                                                  String modalidad, String objetivoGeneral,
                                                  String objetivosEspecificos, String archivoAdjunto) {
        return DocumentData.builder()
                .tipo("FORMATO_A")
                .contenido(contenido)
                .usuarioId(usuarioId)
                .titulo(titulo)
                .modalidad(modalidad)
                .objetivoGeneral(objetivoGeneral)
                .objetivosEspecificos(objetivosEspecificos)
                .archivoAdjunto(archivoAdjunto)
                .build();
    }

    public static DocumentData createAnteproyectoData(String contenido, String usuarioId, String titulo,
                                                      String archivoAdjunto) {
        return DocumentData.builder()
                .tipo("ANTEPROYECTO")
                .contenido(contenido)
                .usuarioId(usuarioId)
                .titulo(titulo)
                .archivoAdjunto(archivoAdjunto)
                .build();
    }

    public static DocumentData createFormatoACorregidoData(String contenido, String usuarioId,
                                                           String observacionesAnteriores) {
        return DocumentData.builder()
                .tipo("FORMATO_A_CORREGIDO")
                .contenido(contenido)
                .usuarioId(usuarioId)
                .metadata(Map.of("observacionesAnteriores", observacionesAnteriores))
                .build();
    }
}
