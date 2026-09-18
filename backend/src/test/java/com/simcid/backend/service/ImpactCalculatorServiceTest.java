package com.simcid.backend.service;

import com.simcid.backend.config.SimcidProperties;
import com.simcid.backend.dto.ImpactBreakdownDTO;
import com.simcid.backend.dto.ImpactScalesDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Confere a matematica do ImpactCalculatorService com as constantes exigidas:
 * 2 L/km, R$ 6,30/L, 25 km/h e 1,8 kg CO2/km.
 */
class ImpactCalculatorServiceTest {

    private static final double DELTA = 1e-6;

    private ImpactCalculatorService impactCalculatorService;

    @BeforeEach
    void setUp() {
        SimcidProperties.Impact impact = new SimcidProperties.Impact(2.0, 6.3, 25.0, 1.8);
        SimcidProperties.Route route = new SimcidProperties.Route(6, 150_000, 4_000);
        impactCalculatorService = new ImpactCalculatorService(new SimcidProperties(impact, route));
    }

    @Test
    void calculatesDailyBreakdownForTenKilometers() {
        ImpactBreakdownDTO breakdown = impactCalculatorService.forDistance(10.0);

        assertEquals(10.0, breakdown.distanceKm(), DELTA);
        assertEquals(20.0, breakdown.fuelLiters(), DELTA);   // 10 km * 2 L/km
        assertEquals(126.0, breakdown.costReais(), DELTA);   // 20 L * R$6,30
        assertEquals(18.0, breakdown.co2Kg(), DELTA);        // 10 km * 1,8 kg/km
        assertEquals(24.0, breakdown.timeMinutes(), DELTA);  // 10 km / 25 km/h * 60
    }

    @Test
    void scalesFourPeriodsCorrectlyFromTheDailyDistance() {
        ImpactScalesDTO scales = impactCalculatorService.scalesFor(10.0);

        assertEquals(20.0, scales.daily().fuelLiters(), DELTA);
        assertEquals(140.0, scales.weekly().fuelLiters(), DELTA);     // 7x
        assertEquals(600.0, scales.monthly().fuelLiters(), DELTA);    // 30x
        assertEquals(7300.0, scales.annual().fuelLiters(), DELTA);    // 365x

        assertEquals(126.0, scales.daily().costReais(), DELTA);
        assertEquals(882.0, scales.weekly().costReais(), DELTA);
        assertEquals(3780.0, scales.monthly().costReais(), DELTA);
        assertEquals(45990.0, scales.annual().costReais(), DELTA);

        assertEquals(18.0, scales.daily().co2Kg(), DELTA);
        assertEquals(126.0, scales.weekly().co2Kg(), DELTA);
        assertEquals(540.0, scales.monthly().co2Kg(), DELTA);
        assertEquals(6570.0, scales.annual().co2Kg(), DELTA);

        assertEquals(24.0, scales.daily().timeMinutes(), DELTA);
        assertEquals(168.0, scales.weekly().timeMinutes(), DELTA);
        assertEquals(720.0, scales.monthly().timeMinutes(), DELTA);
        assertEquals(8760.0, scales.annual().timeMinutes(), DELTA);
    }

    @Test
    void zeroDistanceProducesZeroImpact() {
        ImpactBreakdownDTO breakdown = impactCalculatorService.forDistance(0.0);

        assertEquals(0.0, breakdown.fuelLiters(), DELTA);
        assertEquals(0.0, breakdown.costReais(), DELTA);
        assertEquals(0.0, breakdown.co2Kg(), DELTA);
        assertEquals(0.0, breakdown.timeMinutes(), DELTA);
    }
}
