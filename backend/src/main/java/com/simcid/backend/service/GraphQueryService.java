package com.simcid.backend.service;

import com.simcid.backend.dto.GraphDTO;
import com.simcid.backend.dto.GraphEdgeDTO;
import com.simcid.backend.dto.GraphNodeDTO;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Traduz o grafo em memoria (JGraphT) para DTOs serializaveis, usados pelo
 * cliente para desenhar a malha viaria completa (63 nos / 110 arestas).
 */
@Service
public class GraphQueryService {

    private static final Pattern NODE_PATTERN = Pattern.compile("^N(\\d+)_(\\d+)$");

    private final Graph<String, DefaultWeightedEdge> graph;

    public GraphQueryService(Graph<String, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public GraphDTO getSnapshot() {
        List<GraphNodeDTO> nodes = graph.vertexSet().stream()
                .map(this::toNodeDTO)
                .sorted(Comparator.comparingInt(GraphNodeDTO::row).thenComparingInt(GraphNodeDTO::column))
                .toList();

        List<GraphEdgeDTO> edges = graph.edgeSet().stream()
                .map(edge -> new GraphEdgeDTO(
                        graph.getEdgeSource(edge),
                        graph.getEdgeTarget(edge),
                        graph.getEdgeWeight(edge)))
                .toList();

        return new GraphDTO(nodes, edges);
    }

    private GraphNodeDTO toNodeDTO(String nodeId) {
        Matcher matcher = NODE_PATTERN.matcher(nodeId);
        if (!matcher.matches()) {
            throw new IllegalStateException("Id de no com formato inesperado: " + nodeId);
        }
        int column = Integer.parseInt(matcher.group(1));
        int row = Integer.parseInt(matcher.group(2));
        return new GraphNodeDTO(nodeId, column, row);
    }
}
