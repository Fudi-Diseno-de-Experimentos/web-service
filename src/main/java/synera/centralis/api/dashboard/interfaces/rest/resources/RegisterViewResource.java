package synera.centralis.api.dashboard.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * Resource for registering content view (announcement or event)
 */
public record RegisterViewResource(
        @NotBlank(message = "User ID is required")
        String userId
) {
}