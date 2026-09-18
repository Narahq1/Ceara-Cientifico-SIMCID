package com.simcid.backend.domain;

/**
 * Um ponto selecionavel (escola ou ponto geral da cidade) posicionado sobre um
 * cruzamento exato da malha viaria (ver CityMapData / CityGraphFactory).
 *
 * @param id     identificador curto e estavel usado nas requisicoes da API (ex.: "A", "P4")
 * @param name   nome de exibicao completo
 * @param type   categoria do ponto
 * @param column coluna do cruzamento na malha (0..COLS-1)
 * @param row    linha do cruzamento na malha (0..ROWS-1)
 */
public record PointOfInterest(String id, String name, PointType type, int column, int row) {

    /**
     * Identificador do no do grafo em que este ponto esta localizado,
     * no formato usado por CityGraphFactory (ex.: "N3_5").
     */
    public String nodeId() {
        return "N" + column + "_" + row;
    }
}
