package com.simcid.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Corpo da requisicao de simulacao de rota.
 *
 * @param originId         id do ponto de origem (ex.: "A")
 * @param destinationId    id do ponto de destino (ex.: "H")
 * @param requiredPointIds ids de pontos que a rota deve obrigatoriamente visitar (opcional, pode ser omitido ou vazio)
 */
public record RouteRequestDTO(
        @NotBlank(message = "originId e obrigatorio") String originId,
        @NotBlank(message = "destinationId e obrigatorio") String destinationId,
        List<String> requiredPointIds
) {
    /** Garante uma lista nao-nula para simplificar o RouteService. */
    public List<String> requiredPointIdsOrEmpty() {
        return requiredPointIds == null ? List.of() : requiredPointIds;
    }
}
