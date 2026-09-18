package com.simcid.backend.service;

import com.simcid.backend.config.SimcidProperties;
import com.simcid.backend.dto.PointDTO;
import com.simcid.backend.dto.RouteRequestDTO;
import com.simcid.backend.dto.RouteSimulationResponseDTO;
import com.simcid.backend.exception.InvalidRouteRequestException;
import com.simcid.backend.exception.PointNotFoundException;
import com.simcid.backend.graph.CityGraphFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteServiceTest {

    private RouteService routeService;

    @BeforeEach
    void setUp() {
        Graph<String, DefaultWeightedEdge> graph = CityGraphFactory.buildGraph();
        PointService pointService = new PointService();
        SimcidProperties properties = new SimcidProperties(
                new SimcidProperties.Impact(2.0, 6.3, 25.0, 1.8),
                new SimcidProperties.Route(6, 150_000, 4_000));
        ImpactCalculatorService impactCalculatorService = new ImpactCalculatorService(properties);
        routeService = new RouteService(graph, pointService, impactCalculatorService, properties);
    }

    @Test
    void shortestRouteBetweenAAndHMatchesManhattanDistance() {
        // A esta em (0,0) e H em (8,1): |8-0| + |1-0| = 9 blocos * 0,25 km = 2,25 km
        RouteSimulationResponseDTO response = routeService.simulate(new RouteRequestDTO("A", "H", null));

        assertEquals("A", response.origin().id());
        assertEquals("H", response.destination().id());
        assertEquals(2.25, response.shortestRoute().distanceKm(), 1e-9);
        assertEquals("N0_0", response.shortestRoute().nodePath().get(0));
        assertEquals("N8_1", response.shortestRoute().nodePath().get(response.shortestRoute().nodePath().size() - 1));
    }

    @Test
    void longestRouteIsNeverShorterThanShortestRoute() {
        RouteSimulationResponseDTO response = routeService.simulate(new RouteRequestDTO("A", "H", null));

        assertTrue(response.longestRoute().distanceKm() >= response.shortestRoute().distanceKm());
        assertTrue(response.longestRoute().pathsExplored() > 0);
    }

    @Test
    void longestRoutePathNeverRepeatsANode() {
        RouteSimulationResponseDTO response = routeService.simulate(new RouteRequestDTO("A", "I", null));

        List<String> nodePath = response.longestRoute().nodePath();
        Set<String> distinctNodes = new HashSet<>(nodePath);
        assertEquals(nodePath.size(), distinctNodes.size(), "o caminho mais longo nao deve repetir cruzamentos");
    }

    @Test
    void requiredPointIsVisitedByTheShortestRoute() {
        RouteSimulationResponseDTO response = routeService.simulate(new RouteRequestDTO("A", "H", List.of("D")));

        boolean visitsD = response.shortestRoute().pointOrder().stream()
                .map(PointDTO::id)
                .anyMatch(id -> id.equals("D"));
        assertTrue(visitsD, "a rota deveria passar pelo ponto obrigatorio D");
    }

    @Test
    void unknownPointIdThrowsPointNotFoundException() {
        assertThrows(PointNotFoundException.class,
                () -> routeService.simulate(new RouteRequestDTO("A", "ZZZ", null)));
    }

    @Test
    void sameOriginAndDestinationThrowsInvalidRouteRequestException() {
        assertThrows(InvalidRouteRequestException.class,
                () -> routeService.simulate(new RouteRequestDTO("A", "A", null)));
    }

    @Test
    void tooManyRequiredPointsThrowsInvalidRouteRequestException() {
        List<String> tooMany = List.of("B", "C", "D", "E", "F", "G", "I"); // 7 pontos > limite de 6
        assertThrows(InvalidRouteRequestException.class,
                () -> routeService.simulate(new RouteRequestDTO("A", "H", tooMany)));
    }
}
