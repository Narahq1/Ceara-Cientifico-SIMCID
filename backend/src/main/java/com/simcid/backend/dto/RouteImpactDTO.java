package com.simcid.backend.dto;

/**
 * Impacto completo da simulacao: o impacto de cada rota isoladamente, e a
 * economia obtida ao optar pela menor rota em vez da maior rota encontrada.
 */
public record RouteImpactDTO(
        ImpactScalesDTO shortestRoute,
        ImpactScalesDTO longestRoute,
        ImpactScalesDTO savings
) {
}
