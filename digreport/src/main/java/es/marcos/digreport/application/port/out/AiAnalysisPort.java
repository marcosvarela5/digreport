package es.marcos.digreport.application.port.out;

import es.marcos.digreport.domain.exception.AiAnalysisException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AiAnalysisPort {

    /**
     * Analiza imágenes de un hallazgo arqueológico usando IA
     *
     * @param images Lista de imágenes a analizar
     * @return Resultado del análisis en formato JSON
     * @throws AiAnalysisException si hay error en el análisis
     */
    String analyzeImages(List<MultipartFile> images) throws AiAnalysisException;

}
