package com.simcid.backend.dto;

/**
 * Representacao de um ponto de interesse exposta pela API.
 *
 * @param id     identificador curto (ex.: "A", "P4")
 * @param name   nome de exibicao
 * @param type   categoria (SCHOOL, HEALTH, GOVERNMENT, ...)
 * @param nodeId cruzamento da malha em que o ponto esta localizado (ex.: "N0_0")
 * @param column coluna do cruzamento
 * @param row    linha do cruzamento
 */
public record PointDTO(String id, String name, String type, String nodeId, int column, int row) {
}
