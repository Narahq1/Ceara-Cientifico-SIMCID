package com.simcid.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simcid.backend.dto.RouteRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integracao ponta-a-ponta: sobe o contexto Spring completo e chama
 * os endpoints reais via MockMvc, garantindo que controller, servicos, grafo
 * e serializacao JSON funcionam juntos corretamente.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RouteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthEndpointReportsUp() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void pointsEndpointReturnsAllTwentyOnePoints() throws Exception {
        mockMvc.perform(get("/api/v1/points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(21));
    }

    @Test
    void graphEndpointReturnsFullGridTopology() throws Exception {
        mockMvc.perform(get("/api/v1/graph"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes.length()").value(63))
                .andExpect(jsonPath("$.edges.length()").value(110));
    }

    @Test
    void simulateEndpointReturnsCompleteJsonForAValidRequest() throws Exception {
        RouteRequestDTO request = new RouteRequestDTO("A", "H", null);

        mockMvc.perform(post("/api/v1/routes/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin.id").value("A"))
                .andExpect(jsonPath("$.destination.id").value("H"))
                .andExpect(jsonPath("$.shortestRoute.distanceKm").value(2.25))
                .andExpect(jsonPath("$.longestRoute.distanceKm").isNumber())
                .andExpect(jsonPath("$.impact.shortestRoute.daily.fuelLiters").isNumber())
                .andExpect(jsonPath("$.impact.savings.annual.costReais").isNumber());
    }

    @Test
    void simulateEndpointRespectsRequiredPoints() throws Exception {
        RouteRequestDTO request = new RouteRequestDTO("A", "H", List.of("D"));

        mockMvc.perform(post("/api/v1/routes/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiredPoints.length()").value(1))
                .andExpect(jsonPath("$.requiredPoints[0].id").value("D"));
    }

    @Test
    void simulateEndpointReturns404ForUnknownPoint() throws Exception {
        RouteRequestDTO request = new RouteRequestDTO("A", "ZZZ", null);

        mockMvc.perform(post("/api/v1/routes/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void simulateEndpointReturns400ForSameOriginAndDestination() throws Exception {
        RouteRequestDTO request = new RouteRequestDTO("A", "A", null);

        mockMvc.perform(post("/api/v1/routes/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void simulateEndpointReturns400WhenOriginIsBlank() throws Exception {
        String invalidJson = "{\"originId\":\"\",\"destinationId\":\"H\"}";

        mockMvc.perform(post("/api/v1/routes/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
