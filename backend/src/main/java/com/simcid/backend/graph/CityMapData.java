package com.simcid.backend.graph;

import com.simcid.backend.domain.PointOfInterest;
import com.simcid.backend.domain.PointType;

import java.util.List;

/**
 * Dados estaticos do mapa da cidade do SIMCID: dimensoes da malha viaria e os
 * 21 pontos selecionaveis (10 escolas da aba "Rede Escolar" + 11 pontos gerais
 * da aba "Mapa Geral"), com as MESMAS coordenadas de cruzamento do prototipo
 * original (app.js), garantindo que a topologia do grafo nao mude entre versoes.
 */
public final class CityMapData {

    /** Numero de colunas de cruzamentos da malha (colunas 0..COLS-1). */
    public static final int COLS = 9;

    /** Numero de linhas de cruzamentos da malha (linhas 0..ROWS-1). */
    public static final int ROWS = 7;

    /** Comprimento, em km, de cada trecho de rua entre dois cruzamentos adjacentes. */
    public static final double BLOCK_LENGTH_KM = 0.25;

    /** As 10 escolas da aba "Rede Escolar". */
    public static final List<PointOfInterest> SCHOOLS = List.of(
            new PointOfInterest("A", "EEEP Alfredo Nunes de Melo", PointType.SCHOOL, 0, 0),
            new PointOfInterest("B", "EEM Dr. Hugo de Gouvea Soares", PointType.SCHOOL, 2, 0),
            new PointOfInterest("C", "Educandario Emilia de Lima Pinho", PointType.SCHOOL, 0, 2),
            new PointOfInterest("D", "Balao Magico Escolinha", PointType.SCHOOL, 3, 3),
            new PointOfInterest("E", "EEM Jose Adonias Gurgel de Albuquerque", PointType.SCHOOL, 2, 5),
            new PointOfInterest("F", "EEM Pe. Joao Antonio", PointType.SCHOOL, 6, 3),
            new PointOfInterest("G", "EEM Serafim de Sousa Lima", PointType.SCHOOL, 6, 5),
            new PointOfInterest("H", "Liceu de Acopiara Dep. Francisco Alves Sobrinho", PointType.SCHOOL, 8, 1),
            new PointOfInterest("I", "EEM Joao Moreira Barros", PointType.SCHOOL, 7, 6),
            new PointOfInterest("J", "EEM Elodia Tavares de Souza", PointType.SCHOOL, 5, 5)
    );

    /** Os 11 pontos gerais adicionados na aba "Mapa Geral". */
    public static final List<PointOfInterest> CITY_POINTS = List.of(
            new PointOfInterest("P1", "Prefeitura Municipal", PointType.GOVERNMENT, 4, 2),
            new PointOfInterest("P2", "Polo de Lazer", PointType.LEISURE, 4, 4),
            new PointOfInterest("P3", "Igreja Matriz", PointType.CHURCH, 7, 1),
            new PointOfInterest("P4", "Terminal / Rodoviaria", PointType.TRANSPORT, 0, 3),
            new PointOfInterest("P5", "Mercado Central", PointType.COMMERCE, 5, 4),
            new PointOfInterest("P6", "Delegacia", PointType.SECURITY, 5, 0),
            new PointOfInterest("P7", "Posto de Gasolina", PointType.FUEL, 7, 0),
            new PointOfInterest("P8", "Farmacia", PointType.HEALTH, 4, 3),
            new PointOfInterest("P9", "INSS", PointType.SERVICE, 7, 2),
            new PointOfInterest("P10", "Camara dos Vereadores", PointType.GOVERNMENT, 1, 4),
            new PointOfInterest("P11", "Hospital Municipal", PointType.HEALTH, 8, 0)
    );

    /** Uniao de SCHOOLS + CITY_POINTS: os 21 pontos selecionaveis do "Mapa Geral". */
    public static final List<PointOfInterest> ALL_POINTS =
            java.util.stream.Stream.concat(SCHOOLS.stream(), CITY_POINTS.stream()).toList();

    private CityMapData() {
        // classe utilitaria: apenas dados estaticos, sem instancias
    }
}
