package es.marcos.digreport.application.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public record AiAnalysisResponse(
        @JsonProperty("tipo_probable")
        String tipoProbable,

        @JsonProperty("material_estimado")
        String materialEstimado,

        @JsonProperty("periodo_estimado")
        String periodoEstimado,

        @JsonProperty("confianza")
        Double confianza,  // 0.0 a 1.0

        @JsonProperty("caracteristicas_clave")
        List<String> caracteristicasClave,

        @JsonProperty("descripcion")
        String descripcion,

        @JsonProperty("advertencias")
        String advertencias
) {}