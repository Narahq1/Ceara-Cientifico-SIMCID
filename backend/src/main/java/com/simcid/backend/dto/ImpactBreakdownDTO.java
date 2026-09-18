package com.simcid.backend.dto;

/**
 * Impacto (consumo, custo, emissao e tempo) associado a uma distancia percorrida.
 *
 * @param distanceKm   distancia considerada, em km
 * @param fuelLiters   litros de combustivel consumidos
 * @param costReais    custo em reais
 * @param co2Kg        emissao de CO2 em kg
 * @param timeMinutes  tempo de deslocamento estimado, em minutos
 */
public record ImpactBreakdownDTO(
        double distanceKm,
        double fuelLiters,
        double costReais,
        double co2Kg,
        double timeMinutes
) {
}
