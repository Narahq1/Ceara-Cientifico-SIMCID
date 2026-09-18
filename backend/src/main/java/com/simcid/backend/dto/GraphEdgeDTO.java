package com.simcid.backend.dto;

/** Um trecho de rua (aresta) entre dois cruzamentos adjacentes da malha. */
public record GraphEdgeDTO(String from, String to, double weightKm) {
}
