package com.simcid.backend.dto;

import java.util.List;

/**
 * Resultado da busca pela maior rota (caminho simples, sem repetir cruzamentos)
 * entre origem e destino, dentro do limite de seguranca configurado.
 *
 * @param distanceKm            distancia total em quilometros
 * @param estimatedTimeMinutes  tempo estimado em minutos, na velocidade media configurada
 * @param nodePath              sequencia de cruzamentos percorridos
 * @param pointOrder            pontos de interesse efetivamente visitados, na ordem da rota
 * @param edgeCount             numero de trechos de rua percorridos
 * @param pathsExplored         quantidade de caminhos completos avaliados durante a busca
 * @param searchLimitReached    true se a busca foi interrompida pelo limite de seguranca
 *                              (tempo maximo ou numero maximo de caminhos) antes de esgotar
 *                              todas as combinacoes possiveis; nesse caso o resultado e a
 *                              melhor rota encontrada ate o limite, nao uma garantia matematica de otimo global
 */
public record LongestRouteSummaryDTO(
        double distanceKm,
        double estimatedTimeMinutes,
        List<String> nodePath,
        List<PointDTO> pointOrder,
        int edgeCount,
        long pathsExplored,
        boolean searchLimitReached
) {
}
