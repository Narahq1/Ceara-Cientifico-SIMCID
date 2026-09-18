package com.simcid.backend.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CityGraphFactoryTest {

    @Test
    void gridHas63NodesAnd110Edges() {
        Graph<String, DefaultWeightedEdge> graph = CityGraphFactory.buildGraph();

        // 9 colunas x 7 linhas = 63 cruzamentos
        assertEquals(63, graph.vertexSet().size());

        // horizontais: 8 * 7 = 56 ; verticais: 9 * 6 = 54 ; total = 110
        assertEquals(110, graph.edgeSet().size());
    }

    @Test
    void allEdgesHaveTheUniformBlockWeight() {
        Graph<String, DefaultWeightedEdge> graph = CityGraphFactory.buildGraph();

        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            assertEquals(CityMapData.BLOCK_LENGTH_KM, graph.getEdgeWeight(edge), 1e-9);
        }
    }

    @Test
    void cornerNodesExistAndAreConnected() {
        Graph<String, DefaultWeightedEdge> graph = CityGraphFactory.buildGraph();

        assertTrue(graph.containsVertex("N0_0"));
        assertTrue(graph.containsVertex("N8_6")); // (COLS-1, ROWS-1)
        assertTrue(graph.containsEdge("N0_0", "N1_0"));
        assertTrue(graph.containsEdge("N0_0", "N0_1"));
    }

    @Test
    void gridDimensionsMatchDocumentedMapSize() {
        assertEquals(9, CityMapData.COLS);
        assertEquals(7, CityMapData.ROWS);
        assertEquals(21, CityMapData.ALL_POINTS.size());
    }
}
