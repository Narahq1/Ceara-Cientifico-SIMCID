package com.simcid.backend.graph;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Busca a maior rota (o caminho SIMPLES de maior distancia, isto e, sem repetir
 * nenhum cruzamento) entre uma origem e um destino, percorrendo exaustivamente
 * o espaco de caminhos possiveis via busca em profundidade (DFS) com backtracking.
 *
 * <p><b>Por que ha um limite de seguranca:</b> em um grid de 9x7 cruzamentos o
 * numero de caminhos simples entre dois pontos distantes cresce de forma
 * combinatoria (fenomeno conhecido como "self-avoiding walks"), podendo chegar
 * a centenas de milhares ou milhoes de possibilidades. Enumerar literalmente
 * TODOS sem nenhum limite arriscaria travar a API por dezenas de segundos ou
 * mais. Por isso a busca e exaustiva (DFS classico, sem heuristicas de poda que
 * poderiam descartar o caminho realmente mais longo) mas interrompida por um
 * teto de caminhos completos avaliados e por um teto de tempo, ambos
 * configuraveis em application.yml (simcid.route.*). Quando o teto e atingido,
 * {@link Result#limitReached()} vem true e o resultado retornado e o melhor
 * (maior) caminho encontrado ate aquele ponto da busca.</p>
 */
public final class LongestPathFinder {

    private final Graph<String, DefaultWeightedEdge> graph;
    private final String origin;
    private final String destination;
    private final Set<String> mandatoryNodes;
    private final long maxPathsExplored;
    private final long maxElapsedMillis;

    private long pathsExplored = 0;
    private boolean limitReached = false;
    private double bestDistance = -1.0;
    private List<String> bestPath = null;
    private long startTimeMillis;

    public LongestPathFinder(Graph<String, DefaultWeightedEdge> graph,
                              String origin,
                              String destination,
                              Set<String> mandatoryNodes,
                              long maxPathsExplored,
                              long maxElapsedMillis) {
        this.graph = graph;
        this.origin = origin;
        this.destination = destination;
        this.mandatoryNodes = mandatoryNodes;
        this.maxPathsExplored = maxPathsExplored;
        this.maxElapsedMillis = maxElapsedMillis;
    }

    public Result run() {
        startTimeMillis = System.currentTimeMillis();
        LinkedHashSet<String> visited = new LinkedHashSet<>();
        visited.add(origin);
        dfs(origin, visited, 0.0);
        return new Result(bestPath, Math.max(bestDistance, 0.0), pathsExplored, limitReached);
    }

    private void dfs(String current, LinkedHashSet<String> visited, double distanceSoFar) {
        if (limitReached) {
            return;
        }
        if (pathsExplored >= maxPathsExplored || (System.currentTimeMillis() - startTimeMillis) >= maxElapsedMillis) {
            limitReached = true;
            return;
        }

        if (current.equals(destination)) {
            pathsExplored++;
            if (distanceSoFar > bestDistance && visited.containsAll(mandatoryNodes)) {
                bestDistance = distanceSoFar;
                bestPath = new ArrayList<>(visited);
            }
            return; // uma rota termina ao chegar ao destino; nao continuamos alem dele
        }

        for (DefaultWeightedEdge edge : graph.edgesOf(current)) {
            String next = Graphs.getOppositeVertex(graph, edge, current);
            if (visited.contains(next)) {
                continue; // caminho simples: nunca revisita um cruzamento
            }
            visited.add(next);
            dfs(next, visited, distanceSoFar + graph.getEdgeWeight(edge));
            visited.remove(next);
            if (limitReached) {
                return;
            }
        }
    }

    /**
     * @param bestPath       melhor caminho (lista de nos) encontrado, ou null se nenhum caminho valido foi encontrado
     * @param bestDistance   distancia total do melhor caminho, em km
     * @param pathsExplored  quantidade de caminhos completos (origem->destino) avaliados
     * @param limitReached   true se a busca foi interrompida pelo limite de seguranca antes de esgotar todas as possibilidades
     */
    public record Result(List<String> bestPath, double bestDistance, long pathsExplored, boolean limitReached) {
    }
}
