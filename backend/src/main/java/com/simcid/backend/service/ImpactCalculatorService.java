package com.simcid.backend.service;

import com.simcid.backend.config.SimcidProperties;
import com.simcid.backend.dto.ImpactBreakdownDTO;
import com.simcid.backend.dto.ImpactScalesDTO;
import org.springframework.stereotype.Service;

/**
 * Calcula o impacto (combustivel, custo, CO2 e tempo) de percorrer uma dada
 * distancia, usando as constantes de simcid.impact (application.yml):
 *
 * <ul>
 *   <li>consumo: 2 litros por km</li>
 *   <li>custo do combustivel: R$ 6,30 por litro</li>
 *   <li>velocidade media: 25 km/h</li>
 *   <li>emissao: 1,8 kg de CO2 por km</li>
 * </ul>
 *
 * <p>O impacto "diario" corresponde a distancia percorrida uma vez; semanal,
 * mensal e anual multiplicam essa mesma distancia por 7, 30 e 365,
 * respectivamente, simulando o efeito de repetir o mesmo trajeto todos os dias
 * ao longo do periodo (uso tipico: rota casa-escola feita diariamente).</p>
 */
@Service
public class ImpactCalculatorService {

    private static final int DAYS_PER_WEEK = 7;
    private static final int DAYS_PER_MONTH = 30;
    private static final int DAYS_PER_YEAR = 365;

    private final SimcidProperties.Impact impact;

    public ImpactCalculatorService(SimcidProperties properties) {
        this.impact = properties.impact();
    }

    /** Impacto de uma unica viagem com a distancia informada. */
    public ImpactBreakdownDTO forDistance(double distanceKm) {
        double fuelLiters = distanceKm * impact.fuelConsumptionLitersPerKm();
        double costReais = fuelLiters * impact.costPerLiterReais();
        double co2Kg = distanceKm * impact.co2EmissionKgPerKm();
        double timeMinutes = impact.averageSpeedKmh() > 0
                ? (distanceKm / impact.averageSpeedKmh()) * 60.0
                : 0.0;

        return new ImpactBreakdownDTO(
                round2(distanceKm),
                round2(fuelLiters),
                round2(costReais),
                round2(co2Kg),
                round2(timeMinutes));
    }

    /** Projeta a distancia informada nas quatro escalas: diaria, semanal, mensal e anual. */
    public ImpactScalesDTO scalesFor(double distanceKm) {
        return new ImpactScalesDTO(
                forDistance(distanceKm),
                forDistance(distanceKm * DAYS_PER_WEEK),
                forDistance(distanceKm * DAYS_PER_MONTH),
                forDistance(distanceKm * DAYS_PER_YEAR));
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
