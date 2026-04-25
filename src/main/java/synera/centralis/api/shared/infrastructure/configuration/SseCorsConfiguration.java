package synera.centralis.api.shared.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for Cross-Origin Resource Sharing (CORS)
 * Specifically configured for Server-Sent Events (SSE) endpoints
 * 
 * NOTE: CORS configuration moved to WebSecurityConfiguration to avoid conflicts.
 * This class is kept for documentation purposes but is inactive.
 */
// @Configuration // ← DESACTIVADO: Moved to WebSecurityConfiguration
public class SseCorsConfiguration implements WebMvcConfigurer {
    
    // Configuration moved to WebSecurityConfiguration.java
    // to avoid conflicts between Spring Security CORS and WebMvc CORS
}