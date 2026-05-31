package synera.centralis.api.shared.interfaces.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import synera.centralis.api.shared.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Health Controller Integration Test.
 * <p>
 * Validates that the public health check endpoint behaves correctly and allows unauthenticated access.
 * </p>
 */
@AutoConfigureMockMvc
@DisplayName("Health Check Controller - Integration Test")
class HealthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /health should return HTTP 200 OK and status UP with database HEALTHY")
    void healthCheckReturnsUpAnd200() throws Exception {
        mockMvc.perform(get("/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.services.database").value("HEALTHY"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
