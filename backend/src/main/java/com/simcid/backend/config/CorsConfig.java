package com.simcid.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS liberado para todas as origens em /api/**.
 *
 * <p>Isto e proposital para desenvolvimento local: o app Flutter pode rodar como
 * Flutter Web (porta aleatoria do "flutter run -d chrome"), como app mobile
 * (sem origem HTTP) ou como app desktop. Antes de publicar o backend em producao,
 * restrinja allowedOrigins ao dominio real do front-end.</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
