package es.marcos.digreport.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de beans para comunicación HTTP externa.
 * Necesario para los adaptadores de IA (Gemini, Claude, etc.)
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Bean de RestTemplate para hacer llamadas HTTP a APIs externas
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}