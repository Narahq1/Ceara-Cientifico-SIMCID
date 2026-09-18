package com.simcid.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Parametros de negocio do SIMCID, lidos de application.yml (prefixo "simcid").
 * Mantidos fora do codigo Java para que analistas possam recalibrar o simulador
 * (preco do combustivel, velocidade media, etc.) sem precisar alterar classes.
 */
@ConfigurationProperties(prefix = "simcid")
public record SimcidProperties(Impact impact, Route route) {

    /**
     * Constantes usadas pelo ImpactCalculatorService.
     */
    public record Impact(
            double fuelConsumptionLitersPerKm,
            double costPerLiterReais,
            double averageSpeedKmh,
            double co2EmissionKgPerKm
    ) {
    }

    /**
     * Limites de seguranca usados pelo RouteService para evitar que a busca
     * exaustiva da maior rota (ou combinacoes de pontos obrigatorios) trave o servidor.
     */
    public record Route(
            int maxRequiredPoints,
            long longestRouteMaxPathsExplored,
            long longestRouteMaxElapsedMillis
    ) {
    }
}
