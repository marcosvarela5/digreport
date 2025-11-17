package es.marcos.digreport.infrastructure.external.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.marcos.digreport.application.port.out.AiAnalysisPort;
import es.marcos.digreport.domain.exception.AiAnalysisException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Adaptador que implementa el puerto de análisis IA usando Claude API de Anthropic.
 * Este adaptador encapsula toda la lógica de integración con servicios externos.
 */
//@Component
public class ClaudeAiAdapter implements AiAnalysisPort {

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-sonnet-4-20250514";
    private static final int MAX_TOKENS = 2000;

    @Value("${anthropic.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ClaudeAiAdapter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String analyzeImages(List<MultipartFile> images) throws AiAnalysisException {
        try {
            // Validaciones
            validateImages(images);

            // Construir request
            Map<String, Object> request = buildClaudeRequest(images);

            // Headers
            HttpHeaders headers = buildHeaders();

            // Llamar API
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    CLAUDE_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Extraer y limpiar respuesta JSON
            return extractAndCleanJson(response.getBody());

        } catch (IOException e) {
            throw new AiAnalysisException("Error procesando imágenes", e);
        } catch (Exception e) {
            throw new AiAnalysisException("Error en la comunicación con el servicio de IA: " + e.getMessage(), e);
        }
    }

    private void validateImages(List<MultipartFile> images) throws AiAnalysisException {
        if (images == null || images.isEmpty()) {
            throw new AiAnalysisException("No se proporcionaron imágenes para analizar");
        }

        if (images.size() > 5) {
            throw new AiAnalysisException("Máximo 5 imágenes permitidas");
        }

        for (MultipartFile image : images) {
            if (image.getSize() > 10 * 1024 * 1024) {
                throw new AiAnalysisException("Imagen excede el tamaño máximo de 10MB");
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new AiAnalysisException("Archivo no válido. Solo se permiten imágenes");
            }
        }
    }

    private Map<String, Object> buildClaudeRequest(List<MultipartFile> images) throws IOException {
        List<Map<String, Object>> contenido = new ArrayList<>();

        // Añadir imágenes convertidas a base64
        for (MultipartFile imagen : images) {
            String base64 = Base64.getEncoder().encodeToString(imagen.getBytes());

            Map<String, Object> imageBlock = new HashMap<>();
            imageBlock.put("type", "image");

            Map<String, String> source = new HashMap<>();
            source.put("type", "base64");
            source.put("media_type", imagen.getContentType());
            source.put("data", base64);

            imageBlock.put("source", source);
            contenido.add(imageBlock);
        }

        // Añadir prompt arqueológico
        Map<String, Object> textBlock = new HashMap<>();
        textBlock.put("type", "text");
        textBlock.put("text", getArchaeologicalPrompt());
        contenido.add(textBlock);

        // Construir mensaje
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", contenido);

        // Request completo
        Map<String, Object> request = new HashMap<>();
        request.put("model", MODEL);
        request.put("max_tokens", MAX_TOKENS);
        request.put("messages", List.of(message));

        return request;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String extractAndCleanJson(String responseBody) throws AiAnalysisException {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String textContent = root.path("content").get(0).path("text").asText();

            // Limpiar markdown si existe
            textContent = textContent
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            // Validar que sea JSON válido
            objectMapper.readTree(textContent);

            return textContent;

        } catch (Exception e) {
            throw new AiAnalysisException("Error parseando respuesta de la IA", e);
        }
    }

    private String getArchaeologicalPrompt() {
        return """
            Eres un experto en arqueología y patrimonio histórico español.
            Analiza las imágenes proporcionadas de un hallazgo arqueológico.
            
            REGLAS HEURÍSTICAS A APLICAR:
            
            1. FORMA:
               - Circular/redonda → probablemente moneda, medalla o botón
               - Rectangular/cuadrada → hebilla, placa, azulejo
               - Irregular orgánica → cerámica, fragmento de objeto
            
            2. COLOR Y MATERIAL:
               - Verde-azulado → cobre o bronce oxidado (pátina)
               - Marrón oscuro/negro → hierro oxidado
               - Gris plateado → plomo o plata
               - Rojizo/terracota → cerámica
               - Dorado → bronce sin oxidar, oro o latón
            
            3. FABRICACIÓN:
               - Bordes perfectamente circulares → moderna (máquina de troquel s.XIX+)
               - Bordes irregulares → antigua (pre-industrial, acuñación manual)
               - Superficie muy lisa → industrial moderna
               - Superficie irregular → artesanal antigua
            
            4. TAMAÑO (si puedes estimarlo):
               - < 15mm diámetro → moneda medieval de bajo valor (dinero, meaja)
               - 15-20mm → moneda moderna pequeña (céntimo, real)
               - 20-30mm → moneda estándar (peseta, duro, moneda colonial)
               - > 30mm → medalla, token o moneda conmemorativa
            
            5. GROSOR:
               - Muy fino → moneda moderna
               - Grosor irregular → acuñación manual antigua
            
            6. CONTEXTO ESPAÑOL:
               - Castillos/leones → heráldica castellana (medieval-moderna)
               - Escudos complejos → s.XVIII-XIX
               - Texto en latín → medieval o eclesiástico
               - Texto en español moderno → s.XIX+
            
            Devuelve ÚNICAMENTE un JSON válido con esta estructura (sin markdown):
            {
              "tipo_probable": "moneda|medalla|cerámica|herramienta|objeto metálico|indeterminado",
              "material_estimado": "cobre|bronce|hierro|plata|cerámica|otro",
              "periodo_estimado": "romano|medieval|moderno (s.XVI-XVIII)|contemporáneo (s.XIX-XX)|indeterminado",
              "confianza": 0.7,
              "caracteristicas_clave": [
                "lista de características visuales observadas"
              ],
              "descripcion": "Descripción en español de máximo 300 caracteres",
              "advertencias": "Posibles errores de identificación o factores de incertidumbre"
            }
            
            SÉ CAUTELOSO: Si no estás seguro, indícalo. Usa "probablemente", "posiblemente".
            La confianza debe reflejar tu certeza real.
            """;
    }
}