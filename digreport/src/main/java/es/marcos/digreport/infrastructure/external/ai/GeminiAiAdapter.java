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
            System.out.println("=== INICIO ANÁLISIS GEMINI ===");
            System.out.println("Número de imágenes: " + images.size());

            validateImages(images);
            System.out.println("✓ Imágenes validadas");

            Map<String, Object> request = buildGeminiRequest(images);
            System.out.println("✓ Request construido para Gemini");

            // Añadir API key en la URL
            String url = GEMINI_API_URL + "?key=" + apiKey;
            System.out.println("URL: " + GEMINI_API_URL);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            System.out.println("🔄 Enviando request a Gemini... (puede tardar 30-60 segundos)");
            long startTime = System.currentTimeMillis();

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            long endTime = System.currentTimeMillis();
            System.out.println("✓ Respuesta recibida de Gemini en " + (endTime - startTime) + "ms");
            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Body length: " + (response.getBody() != null ? response.getBody().length() : 0) + " chars");

            String result = extractAndCleanJson(response.getBody());
            System.out.println("✓ JSON extraído y limpiado correctamente");
            System.out.println("=== FIN ANÁLISIS GEMINI ===");

            return result;

        } catch (IOException e) {
            System.err.println("❌ ERROR procesando imágenes:");
            e.printStackTrace();
            throw new AiAnalysisException("Error procesando imágenes", e);
        } catch (Exception e) {
            System.err.println("❌ ERROR comunicando con Gemini:");
            e.printStackTrace();
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
            if (image.getSize() == 0) {
                throw new AiAnalysisException("Una de las imágenes está vacía");
            }

            if (image.getSize() > 10 * 1024 * 1024) {
                throw new AiAnalysisException("Imagen excede 10MB");
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
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
            System.out.println("=== EXTRAYENDO JSON DE RESPUESTA ===");

            JsonNode root = objectMapper.readTree(responseBody);
            System.out.println("✓ Response parseado como JSON");
            System.out.println("Response keys: " + root.fieldNames());

            // Navegar por la estructura de respuesta de Gemini
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            System.out.println("✓ Texto extraído (length: " + text.length() + ")");

            // Limpiar markdown si existe
            text = text.replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            System.out.println("✓ Markdown limpiado");
            System.out.println("JSON limpio: " + text.substring(0, Math.min(200, text.length())) + "...");

            // Validar que sea JSON válido
            objectMapper.readTree(text);
            System.out.println("✓ JSON validado correctamente");

            return text;

        } catch (Exception e) {
            System.err.println("❌ ERROR parseando respuesta:");
            System.err.println("Response body: " + (responseBody != null ? responseBody.substring(0, Math.min(500, responseBody.length())) : "null"));
            e.printStackTrace();
            throw new AiAnalysisException("Error parseando respuesta de Gemini", e);
        }
    }

    private String getArchaeologicalPrompt() {
        return """
        Eres un arqueólogo experto en numismática y patrimonio histórico español.
        Analiza las imágenes proporcionadas de una moneda española aplicando las siguientes reglas heurísticas.

        REGLAS HEURÍSTICAS GENERALES:
        - Forma: circular → moneda; circular deteriorado → moneda antigua; irregular → fragmento.
        - Material: cobre/bronce (pátina verde), plata (gris plateado), oro/latón (dorado), hierro/plomo (oscuro).
        - Si el objeto identificado es una moneda → no usar "bronce puro", emplear cobre, vellón o latón.
        - Si el objeto identificado es otro hallazgo (herramienta, arma, medalla, botón, sello, vasija, estatua) → sí puede clasificarse como bronce.
        - Bordes: perfectos → modernos (s.XIX+); irregulares → antiguos (pre-industrial).
        - Valor: moderno → texto arqueado con cifras arábigas; antiguo → números romanos o símbolos.
        - Año de acuñación: moderno → parte inferior; antiguo → posición variable.

        REGLAS POR ÉPOCA:

        1. SIGLO XV-XVI (Reyes Católicos, Austrias tempranos):
           - Escudos con yugo y flechas, símbolos heráldicos.
           - Leyendas en latín o castellano antiguo.
           - Monedas de vellón, cobre y plata.

        2. SIGLO XVII-XVIII (Austrias tardíos y Borbones iniciales):
           - Escudos complejos con cuarteles múltiples.
           - Monedas de plata con busto real.
           - Valor indicado en números romanos o abreviaturas.

        3. SIGLO XIX (Isabel II, Amadeo I, Alfonso XII/XIII):
           - Escudo real con corona y cuarteles.
           - Leyenda "ISABEL II POR LA GRACIA DE DIOS" u otras similares.
           - Año en parte inferior, valor en perímetro arqueado.
           - Monedas reselladas frecuentes en periodos de crisis.

        4. SIGLO XX (Alfonso XIII tardío, II República, Franco):
           - Escudos republicanos o símbolos franquistas.
           - Valor en cifras arábigas, texto arqueado.
           - Materiales variados: aluminio, cobre-níquel, plata.

        5. SIGLO XXI (Euro):
           - Cara común europea + cara nacional española.
           - Valor en cifras arábigas.
           - Año en parte inferior.

        Devuelve ÚNICAMENTE un JSON válido con esta estructura (sin markdown, sin ```):
        {
          "tipo_probable": "moneda",
          "material_estimado": {
                    "moneda": "cobre|vellon|laton|plata|oro|plomo|aluminio|cobre-niquel|hierro|otro",
                    "otros_objetos": "cobre|bronce|plata|oro|plomo|hierro|cerámica|piedra|hueso|madera|otro"
                  } 
          "periodo_estimado": "siglo XV|siglo XVI|siglo XVII|siglo XVIII|siglo XIX|siglo XX|siglo XXI|indeterminado",
          "confianza": 0.0-1.0 (máx 2 decimales),
          "caracteristicas_clave": ["lista de características observadas"],
          "descripcion": "Texto conciso en español, máximo 300 caracteres",
          "advertencias": "Posibles errores de identificación o incertidumbre contextuales",
          "detalles_moneda": {
            "resellada": true|false|indeterminado,
            "anio_acunacion": "####|indeterminado",
            "valor": "texto arqueado|numeros romanos|simbolos|indeterminado",
            "fecha_legible": true|false
          },
          "forma_borde": "circular_perfecto|circular_deteriorado|irregular|indeterminado"
        }

        INSTRUCCIONES:
        - Sé cauto: usa expresiones como "probablemente" o "posiblemente" si no estás seguro.
        - La confianza debe reflejar tu certeza real.
        - Todos los campos deben estar presentes en el JSON, incluso si son "indeterminado".
        - No añadas texto fuera del JSON.
        """;
    }



}