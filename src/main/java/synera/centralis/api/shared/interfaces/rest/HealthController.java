package synera.centralis.api.shared.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health Check Controller.
 * <p>
 * Provides a public endpoint to check the system's operational health, including the database status.
 * </p>
 */
@RestController
@RequestMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Health", description = "Endpoint for system health checks")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, String> services = new LinkedHashMap<>();
        
        try {
            // Validación rápida de la base de datos
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            
            response.put("status", "UP");
            response.put("timestamp", Instant.now().toString());
            
            services.put("database", "HEALTHY");
            response.put("services", services);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("timestamp", Instant.now().toString());
            
            services.put("database", "UNHEALTHY");
            response.put("services", services);
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}
