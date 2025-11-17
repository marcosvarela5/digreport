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
 * Adaptador que implementa análisis de IA usando Google Gemini (GRATIS).
 * Usa Gemini 1.5 Flash que tiene visión y es gratuito.
 */
@Component
public class GeminiAiAdapter implements AiAnalysisPort {

    // Modelo Gemini 1.5 Flash con visión (gratis)
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @Value("${google.gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiAiAdapter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String analyzeImages(List<MultipartFile> images) throws AiAnalysisException {
        try {
            validateImages(images);

            Map<String, Object> request = buildGeminiRequest(images);

            // Añadir API key en la URL
            String url = GEMINI_API_URL + "?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return extractAndCleanJson(response.getBody());

        } catch (IOException e) {
            throw new AiAnalysisException("Error procesando imágenes", e);
        } catch (Exception e) {
            throw new AiAnalysisException("Error comunicando con Gemini: " + e.getMessage(), e);
        }
    }

    private void validateImages(List<MultipartFile> images) throws AiAnalysisException {
        if (images == null || images.isEmpty()) {
            throw new AiAnalysisException("No se proporcionaron imágenes");
        }
        if (images.size() > 5) {
            throw new AiAnalysisException("Máximo 5 imágenes permitidas");
        }

        for (MultipartFile image : images) {
            //if (image.isEmpty()) {
             //   throw new AiAnalysisException("Una de las imágenes está vacía");
            //}

            if (image.getSize() > 10 * 1024 * 1024) {
                throw new AiAnalysisException("Imagen excede 10MB");
            }

            String contentType = image.getContentType();

            // ✅ LOG PARA VER QUÉ ESTÁ PASANDO
            System.out.println("ContentType recibido: [" + contentType + "]");

            // ✅ VALIDACIÓN MÁS PERMISIVA (temporal)
            if (contentType == null || contentType.isEmpty()) {
                System.out.println("⚠️ ContentType nulo/vacío, intentando determinar por extensión");
                String filename = image.getOriginalFilename();
                if (filename != null &&
                        (filename.toLowerCase().endsWith(".jpg") ||
                                filename.toLowerCase().endsWith(".jpeg") ||
                                filename.toLowerCase().endsWith(".png") ||
                                filename.toLowerCase().endsWith(".webp"))) {
                    // OK, es imagen por extensión
                    continue;
                } else {
                    throw new AiAnalysisException("Tipo de archivo no válido: " + filename);
                }
            }

            if (!contentType.startsWith("image/")) {
                throw new AiAnalysisException("Solo se permiten imágenes. Recibido: " + contentType);
            }
        }
    }

    private Map<String, Object> buildGeminiRequest(List<MultipartFile> images) throws IOException {
        List<Map<String, Object>> parts = new ArrayList<>();

        // PRIMERO: Añadir el prompt de texto
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", getArchaeologicalPrompt());
        parts.add(textPart);

        // DESPUÉS: Añadir las imágenes en base64
        for (MultipartFile image : images) {
            String base64 = Base64.getEncoder().encodeToString(image.getBytes());

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mime_type", image.getContentType());
            inlineData.put("data", base64);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inline_data", inlineData);
            parts.add(imagePart);
        }

        // Construir la estructura completa
        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);

        Map<String, Object> request = new HashMap<>();
        request.put("contents", List.of(content));

        return request;
    }

    private String extractAndCleanJson(String responseBody) throws AiAnalysisException {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            // Navegar por la estructura de respuesta de Gemini
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // Limpiar markdown si existe
            text = text.replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            // Validar que sea JSON válido
            objectMapper.readTree(text);

            return text;

        } catch (Exception e) {
            throw new AiAnalysisException("Error parseando respuesta de Gemini", e);
        }
    }

    private String getArchaeologicalPrompt() {
        return """
            Eres un experto en arqueología y patrimonio histórico español.
            Analiza las imágenes proporcionadas de un hallazgo arqueológico.
            
            REGLAS HEURÍSTICAS:
            
            1. FORMA:
               - Circular/redonda → probablemente moneda, medalla o botón
               - Rectangular → hebilla, placa, azulejo
               - Irregular orgánica → cerámica, fragmento
            
            2. COLOR Y MATERIAL:
               - Verde-azulado → cobre o bronce oxidado (pátina)
               - Marrón oscuro/negro → hierro oxidado
               - Gris plateado → plomo o plata
               - Rojizo/terracota → cerámica
               - Dorado → bronce sin oxidar, oro o latón
            
            3. FABRICACIÓN:
               - Bordes perfectamente circulares → moderna (máquina s.XIX+)
               - Bordes irregulares → antigua (pre-industrial, manual)
               - Superficie lisa → industrial moderna
               - Superficie irregular → artesanal antigua
            
            4. TAMAÑO:
               - < 15mm → moneda medieval bajo valor
               - 15-20mm → moneda moderna pequeña
               - 20-30mm → moneda estándar
               - > 30mm → medalla o conmemorativa
            
            5. CONTEXTO ESPAÑOL:
               - Castillos/leones → heráldica castellana
               - Escudos complejos → s.XVIII-XIX
               - Texto en latín → medieval
               - Texto español moderno → s.XIX+
            
            Devuelve ÚNICAMENTE un JSON válido con esta estructura (sin markdown, sin ```):
            {
              "tipo_probable": "moneda|medalla|cerámica|herramienta|objeto metálico|indeterminado",
              "material_estimado": "cobre|bronce|hierro|plata|cerámica|otro",
              "periodo_estimado": "romano|medieval|moderno (s.XVI-XVIII)|contemporáneo (s.XIX-XX)|indeterminado",
              "confianza": 0.7,
              "caracteristicas_clave": [
                "lista de características observadas"
              ],
              "descripcion": "Descripción en español de máximo 300 caracteres para el usuario",
              "advertencias": "Posibles errores de identificación o incertidumbre"
            }
            
            SÉ CAUTELOSO: Si no estás seguro, indícalo. Usa "probablemente", "posiblemente".
            La confianza debe reflejar tu certeza real (0.0 a 1.0).
            """;
    }
}