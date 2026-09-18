package com.simcid.backend.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Constroi o grafo nao-direcionado e ponderado que representa a malha viaria
 * do SIMCID: um grid ortogonal de {@value CityMapData#COLS} x {@value CityMapData#ROWS}
 * cruzamentos (63 vertices), ligados por 110 arestas (trechos de rua),
 * cada uma com peso fixo de {@value CityMapData#BLOCK_LENGTH_KM} km.
 *
 * <p>Os vertices sao identificados como "N{coluna}_{linha}" (ex.: "N3_5"),
 * exatamente como no prototipo original, para que os pontos de interesse
 * (CityMapData) caiam sempre sobre um cruzamento valido.</p>
 */
@Configuration
public class CityGraphFactory {

    /**
     * Expoe o grafo da malha viaria como bean singleton, reutilizado por
     * todos os servicos (RouteService, GraphQueryService, etc.).
     */
    @Bean
    public Graph<String, DefaultWeightedEdge> cityStreetGraph() {
        return buildGraph();
    }

    public static Graph<String, DefaultWeightedEdge> buildGraph() {
        Graph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        for (int row = 0; row < CityMapData.ROWS; row++) {
            for (int col = 0; col < CityMapData.COLS; col++) {
                graph.addVertex(nodeId(col, row));
            }
        }

        // Trechos horizontais: liga (col,row) a (col+1,row)
        for (int row = 0; row < CityMapData.ROWS; row++) {
            for (int col = 0; col < CityMapData.COLS - 1; col++) {
                addStreet(graph, nodeId(col, row), nodeId(col + 1, row));
            }
        }

        // Trechos verticais: liga (col,row) a (col,row+1)
        for (int col = 0; col < CityMapData.COLS; col++) {
            for (int row = 0; row < CityMapData.ROWS - 1; row++) {
                addStreet(graph, nodeId(col, row), nodeId(col, row + 1));
            }
        }

        return graph;
    }

    /** Identificador de vertice no formato "N{coluna}_{linha}". */
    public static String nodeId(int column, int row) {
        return "N" + column + "_" + row;
    }

    private static void addStreet(Graph<String, DefaultWeightedEdge> graph, String a, String b) {
        DefaultWeightedEdge edge = graph.addEdge(a, b);
        graph.setEdgeWeight(edge, CityMapData.BLOCK_LENGTH_KM);
    }
}
