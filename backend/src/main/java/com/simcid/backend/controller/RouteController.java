package com.simcid.backend.controller;

import com.simcid.backend.dto.GraphDTO;
import com.simcid.backend.dto.PointDTO;
import com.simcid.backend.dto.RouteRequestDTO;
import com.simcid.backend.dto.RouteSimulationResponseDTO;
import com.simcid.backend.service.GraphQueryService;
import com.simcid.backend.service.PointService;
import com.simcid.backend.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * API REST do SIMCID.
 *
 * <ul>
 *   <li>GET  /api/v1/health                - status simples do servico</li>
 *   <li>GET  /api/v1/points                - os 21 pontos selecionaveis (escolas + pontos gerais)</li>
 *   <li>GET  /api/v1/points/{id}           - um ponto especifico</li>
 *   <li>GET  /api/v1/graph                 - a malha viaria completa (63 nos / 110 arestas)</li>
 *   <li>POST /api/v1/routes/simulate       - simula a menor e a maior rota entre dois pontos e o impacto</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1")
public class RouteController {

    private final PointService pointService;
    private final GraphQueryService graphQueryService;
    private final RouteService routeService;

    public RouteController(PointService pointService, GraphQueryService graphQueryService, RouteService routeService) {
        this.pointService = pointService;
        this.graphQueryService = graphQueryService;
        this.routeService = routeService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "simcid-backend");
    }

    @GetMapping("/points")
    public List<PointDTO> getAllPoints() {
        return pointService.getAllPoints();
    }

    @GetMapping("/points/{id}")
    public PointDTO getPoint(@PathVariable String id) {
        return pointService.getDTOById(id);
    }

    @GetMapping("/graph")
    public GraphDTO getGraph() {
        return graphQueryService.getSnapshot();
    }

    @PostMapping("/routes/simulate")
    public ResponseEntity<RouteSimulationResponseDTO> simulate(@Valid @RequestBody RouteRequestDTO request) {
        return ResponseEntity.ok(routeService.simulate(request));
    }
}
