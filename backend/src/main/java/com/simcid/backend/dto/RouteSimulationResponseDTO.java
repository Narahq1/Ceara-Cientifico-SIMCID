package com.simcid.backend.dto;

import java.util.List;

/**
 * JSON completo devolvido por POST /api/v1/routes/simulate: origem, destino,
 * pontos obrigatorios, a menor rota (Dijkstra), a maior rota encontrada
 * (busca exaustiva de caminhos simples) e o impacto (diario/semanal/mensal/anual).
 */
public record RouteSimulationResponseDTO(
        PointDTO origin,
        PointDTO destination,
        List<PointDTO> requiredPoints,
        RouteSummaryDTO shortestRoute,
        LongestRouteSummaryDTO longestRoute,
        RouteImpactDTO impact
) {
}
