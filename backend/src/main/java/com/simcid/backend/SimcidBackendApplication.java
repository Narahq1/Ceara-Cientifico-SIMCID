package com.simcid.backend;

import com.simcid.backend.config.SimcidProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Ponto de entrada da API REST do SIMCID (Simulador de Rotas Urbanas).
 *
 * <p>Sobe um servidor embutido (Tomcat) na porta configurada em application.yml
 * (padrao 8080) e expoe os endpoints definidos em RouteController.</p>
 */
@SpringBootApplication
@EnableConfigurationProperties(SimcidProperties.class)
public class SimcidBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimcidBackendApplication.class, args);
    }
}
