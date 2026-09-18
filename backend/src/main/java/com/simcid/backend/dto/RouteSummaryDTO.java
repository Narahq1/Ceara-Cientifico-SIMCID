package com.simcid.backend.dto;

import java.util.List;

/**
 * Resultado de uma rota calculada (usado para a menor rota).
 *
 * @param distanceKm            distancia total em quilometros
 * @param estimatedTimeMinutes  tempo estimado em minutos, na velocidade media configurada
 * @param nodePath               sequencia de cruzamentos percorridos (N{col}_{row})
 * @param pointOrder             pontos de interesse efetivamente visitados, na ordem da rota
 * @param edgeCount               numero de trechos de rua percorridos
 */
public record RouteSummaryDTO(
        double distanceKm,
        double estimatedTimeMinutes,
        List<String> nodePath,
        List<PointDTO> pointOrder,
        int edgeCount
) {
}
