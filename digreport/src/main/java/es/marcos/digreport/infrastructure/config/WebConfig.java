package es.marcos.digreport.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Para Windows, usa file:/// con barras normales
        String location = "file:///" + uploadDir.replace("\\", "/") + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);

        System.out.println("📁 Sirviendo archivos desde: " + location);
    }
}