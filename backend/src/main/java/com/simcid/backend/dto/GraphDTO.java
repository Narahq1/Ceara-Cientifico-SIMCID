package com.simcid.backend.dto;

import java.util.List;

/** Malha viaria completa (63 nos / 110 arestas), usada para desenhar o mapa no cliente. */
public record GraphDTO(List<GraphNodeDTO> nodes, List<GraphEdgeDTO> edges) {
}
