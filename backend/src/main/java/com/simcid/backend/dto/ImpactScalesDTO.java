package com.simcid.backend.dto;

/**
 * O mesmo impacto projetado em quatro escalas de tempo, assumindo um
 * deslocamento (ida) por dia ao longo do periodo: diaria, semanal (7x),
 * mensal (30x) e anual (365x).
 */
public record ImpactScalesDTO(
        ImpactBreakdownDTO daily,
        ImpactBreakdownDTO weekly,
        ImpactBreakdownDTO monthly,
        ImpactBreakdownDTO annual
) {
}
