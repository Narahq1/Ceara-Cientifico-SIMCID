package com.simcid.backend.service;

import com.simcid.backend.config.SimcidProperties;
import com.simcid.backend.domain.PointOfInterest;
import com.simcid.backend.dto.ImpactScalesDTO;
import com.simcid.backend.dto.LongestRouteSummaryDTO;
import com.simcid.backend.dto.PointDTO;
import com.simcid.backend.dto.RouteImpactDTO;
import com.simcid.backend.dto.RouteRequestDTO;
import com.simcid.backend.dto.RouteSimulationResponseDTO;
import com.simcid.backend.dto.RouteSummaryDTO;
import com.simcid.backend.exception.InvalidRouteRequestException;
import com.simcid.backend.exception.RouteNotFoundException;
import com.simcid.backend.graph.LongestPathFinder;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servico central do SIMCID: calcula a menor rota (algoritmo de Dijkstra, via
 * JGraphT) e a maior rota (busca exaustiva e limitada de caminhos simples,
 * ver {@link LongestPathFinder}) entre dois pontos de interesse, e orquestra
 * a simulacao completa (rotas + impacto) devolvida pela API.
 */
@Service
public class RouteService {

    private final Graph<String, DefaultWeightedEdge> graph;
    private final ShortestPathAlgorithm<String, DefaultWeightedEdge> dijkstra;
    private final PointService pointService;
    private final ImpactCalculatorService impactCalculatorService;
    private final SimcidProperties properties;

    public RouteService(Graph<String, DefaultWeightedEdge> graph,
                         PointService pointService,
                         ImpactCalculatorService impactCalculatorService,
                         SimcidProperties properties) {
        this.graph = graph;
        this.dijkstra = new DijkstraShortestPath<>(graph);
        this.pointService = pointService;
        this.impactCalculatorService = impactCalculatorService;
        this.properties = properties;
    }

    /**
     * Executa a simulacao completa: valida a requisicao, calcula a menor e a
     * maior rota entre origem e destino (passando pelos pontos obrigatorios,
     * se houver) e projeta o impacto de cada rota e da economia entre elas.
     */
    public RouteSimulationResponseDTO simulate(RouteRequestDTO request) {
        List<String> requiredIds = request.requiredPointIdsOrEmpty();
        validateRequest(request.originId(), request.destinationId(), requiredIds);

        PointOfInterest origin = pointService.getById(request.originId());
        PointOfInterest destination = pointService.getById(request.destinationId());
        List<PointOfInterest> requiredPoints = requiredIds.stream()
                .map(pointService::getById)
                .toList();

        RouteSummaryDTO shortestRoute = computeShortestRoute(origin, destination, requiredPoints);
        LongestRouteSummaryDTO longestRoute = computeLongestRoute(origin, destination, requiredPoints);

        ImpactScalesDTO shortestImpact = impactCalculatorService.scalesFor(shortestRoute.distanceKm());
        ImpactScalesDTO longestImpact = impactCalculatorService.scalesFor(longestRoute.distanceKm());
        double savedDistanceKm = Math.max(0.0, longestRoute.distanceKm() - shortestRoute.distanceKm());
        ImpactScalesDTO savingsImpact = impactCalculatorService.scalesFor(savedDistanceKm);

        return new RouteSimulationResponseDTO(
                pointService.toDTO(origin),
                pointService.toDTO(destination),
                requiredPoints.stream().map(pointService::toDTO).toList(),
                shortestRoute,
                longestRoute,
                new RouteImpactDTO(shortestImpact, longestImpact, savingsImpact));
    }

    // ------------------------------------------------------------------
    // Menor rota (Dijkstra)
    // ------------------------------------------------------------------

    private RouteSummaryDTO computeShortestRoute(PointOfInterest origin, PointOfInterest destination,
                                                  List<PointOfInterest> required) {
        List<PointOfInterest> waypoints = bestWaypointOrder(origin, destination, required);

        List<String> fullPath = new ArrayList<>();
        double totalDistanceKm = 0.0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            GraphPath<String, DefaultWeightedEdge> leg = shortestLeg(waypoints.get(i).nodeId(), waypoints.get(i + 1).nodeId());
            appendLeg(fullPath, leg.getVertexList());
            totalDistanceKm += leg.getWeight();
        }

        return buildRouteSummary(fullPath, totalDistanceKm);
    }

    /**
     * Quando ha pontos obrigatorios, testa todas as ordens possiveis de
     * visita-los (permutacoes) e escolhe a sequencia origem -&gt; ... -&gt; destino
     * de menor distancia total. O numero de pontos obrigatorios e limitado
     * (ver simcid.route.max-required-points) porque o custo cresce em fatorial.
     */
    private List<PointOfInterest> bestWaypointOrder(PointOfInterest origin, PointOfInterest destination,
                                                      List<PointOfInterest> required) {
        List<PointOfInterest> distinctRequired = required.stream()
                .filter(p -> !p.id().equals(origin.id()) && !p.id().equals(destination.id()))
                .distinct()
                .toList();

        if (distinctRequired.isEmpty()) {
            return List.of(origin, destination);
        }

        List<PointOfInterest> best = null;
        double bestDistance = Double.MAX_VALUE;
        for (List<PointOfInterest> permutation : permutationsOf(distinctRequired)) {
            List<PointOfInterest> candidate = new ArrayList<>(permutation.size() + 2);
            candidate.add(origin);
            candidate.addAll(permutation);
            candidate.add(destination);

            double distance = totalLegDistance(candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }
        return best;
    }

    private double totalLegDistance(List<PointOfInterest> waypoints) {
        double total = 0.0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            total += shortestLeg(waypoints.get(i).nodeId(), waypoints.get(i + 1).nodeId()).getWeight();
        }
        return total;
    }

    private GraphPath<String, DefaultWeightedEdge> shortestLeg(String fromNode, String toNode) {
        GraphPath<String, DefaultWeightedEdge> path = dijkstra.getPath(fromNode, toNode);
        if (path == null) {
            throw new RouteNotFoundException("Nao existe caminho entre os cruzamentos " + fromNode + " e " + toNode + ".");
        }
        return path;
    }

    // ------------------------------------------------------------------
    // Maior rota (busca exaustiva limitada de caminhos simples)
    // ------------------------------------------------------------------

    private LongestRouteSummaryDTO computeLongestRoute(PointOfInterest origin, PointOfInterest destination,
                                                         List<PointOfInterest> required) {
        Set<String> mandatoryNodes = required.stream()
                .filter(p -> !p.id().equals(origin.id()) && !p.id().equals(destination.id()))
                .map(PointOfInterest::nodeId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        LongestPathFinder finder = new LongestPathFinder(
                graph,
                origin.nodeId(),
                destination.nodeId(),
                mandatoryNodes,
                properties.route().longestRouteMaxPathsExplored(),
                properties.route().longestRouteMaxElapsedMillis());

        LongestPathFinder.Result result = finder.run();

        if (result.bestPath() == null) {
            throw new RouteNotFoundException(
                    "Nao foi encontrado nenhum caminho simples entre " + origin.name() + " e " + destination.name()
                            + (mandatoryNodes.isEmpty() ? "." : " que passe por todos os pontos obrigatorios."));
        }

        List<PointDTO> pointOrder = derivePointOrder(result.bestPath());
        double timeMinutes = averageSpeedKmh() > 0 ? (result.bestDistance() / averageSpeedKmh()) * 60.0 : 0.0;

        return new LongestRouteSummaryDTO(
                round2(result.bestDistance()),
                round2(timeMinutes),
                List.copyOf(result.bestPath()),
                pointOrder,
                result.bestPath().size() - 1,
                result.pathsExplored(),
                result.limitReached());
    }

    // ------------------------------------------------------------------
    // Utilitarios comuns
    // ------------------------------------------------------------------

    private void validateRequest(String originId, String destinationId, List<String> requiredIds) {
        if (originId.equals(destinationId)) {
            throw new InvalidRouteRequestException("Origem e destino devem ser pontos diferentes.");
        }
        int maxRequired = properties.route().maxRequiredPoints();
        if (requiredIds.size() > maxRequired) {
            throw new InvalidRouteRequestException(
                    "Numero de pontos obrigatorios (" + requiredIds.size() + ") excede o limite suportado (" + maxRequired + ").");
        }
    }

    private RouteSummaryDTO buildRouteSummary(List<String> nodePath, double totalDistanceKm) {
        List<PointDTO> pointOrder = derivePointOrder(nodePath);
        double timeMinutes = averageSpeedKmh() > 0 ? (totalDistanceKm / averageSpeedKmh()) * 60.0 : 0.0;
        return new RouteSummaryDTO(
                round2(totalDistanceKm),
                round2(timeMinutes),
                List.copyOf(nodePath),
                pointOrder,
                nodePath.size() - 1);
    }

    /** Concatena uma nova perna de rota, evitando duplicar o cruzamento de fronteira entre pernas. */
    private void appendLeg(List<String> fullPath, List<String> legNodes) {
        if (fullPath.isEmpty()) {
            fullPath.addAll(legNodes);
        } else {
            fullPath.addAll(legNodes.subList(1, legNodes.size()));
        }
    }

    /** Traduz uma sequencia de cruzamentos para os pontos de interesse efetivamente visitados, na ordem da rota. */
    private List<PointDTO> derivePointOrder(List<String> nodePath) {
        Map<String, PointOfInterest> byNodeId = pointService.allPointsByNodeId();
        List<PointDTO> order = new ArrayList<>();
        String lastId = null;
        for (String node : nodePath) {
            PointOfInterest point = byNodeId.get(node);
            if (point != null && !point.id().equals(lastId)) {
                order.add(pointService.toDTO(point));
                lastId = point.id();
            }
        }
        return order;
    }

    private double averageSpeedKmh() {
        return properties.impact().averageSpeedKmh();
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static <T> List<List<T>> permutationsOf(List<T> items) {
        if (items.size() <= 1) {
            return new ArrayList<>(List.of(new ArrayList<>(items)));
        }
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            T picked = items.get(i);
            List<T> rest = new ArrayList<>(items);
            rest.remove(i);
            for (List<T> tail : permutationsOf(rest)) {
                List<T> permutation = new ArrayList<>();
                permutation.add(picked);
                permutation.addAll(tail);
                result.add(permutation);
            }
        }
        return result;
    }
}
