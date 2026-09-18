package com.simcid.backend.service;

import com.simcid.backend.domain.PointOfInterest;
import com.simcid.backend.dto.PointDTO;
import com.simcid.backend.exception.PointNotFoundException;
import com.simcid.backend.graph.CityMapData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Consulta ao catalogo dos 21 pontos de interesse do SIMCID (10 escolas + 11 pontos gerais).
 */
@Service
public class PointService {

    private final Map<String, PointOfInterest> byId;
    private final Map<String, PointOfInterest> byNodeId;

    public PointService() {
        this.byId = CityMapData.ALL_POINTS.stream()
                .collect(Collectors.toMap(PointOfInterest::id, Function.identity()));
        this.byNodeId = CityMapData.ALL_POINTS.stream()
                .collect(Collectors.toMap(PointOfInterest::nodeId, Function.identity()));
    }

    public List<PointDTO> getAllPoints() {
        return CityMapData.ALL_POINTS.stream().map(this::toDTO).toList();
    }

    public PointOfInterest getById(String id) {
        PointOfInterest point = byId.get(id);
        if (point == null) {
            throw new PointNotFoundException(id);
        }
        return point;
    }

    public PointDTO getDTOById(String id) {
        return toDTO(getById(id));
    }

    /** Mapa auxiliar (cruzamento -> ponto) usado pelo RouteService para reconstruir a ordem dos pontos visitados. */
    public Map<String, PointOfInterest> allPointsByNodeId() {
        return byNodeId;
    }

    public PointDTO toDTO(PointOfInterest point) {
        return new PointDTO(point.id(), point.name(), point.type().name(), point.nodeId(), point.column(), point.row());
    }
}
