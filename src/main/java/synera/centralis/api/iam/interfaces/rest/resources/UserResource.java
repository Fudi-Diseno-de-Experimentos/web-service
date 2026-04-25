package synera.centralis.api.iam.interfaces.rest.resources;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * User resource for IAM context
 * Contains authentication and authorization data only
 * Profile data is managed by Profile context
 */
@Schema(description = "User authentication information")
public record UserResource(
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @Schema(description = "Username", example = "john.doe")
    String username,
    
    @Schema(description = "User roles")
    List<String> roles,
    
    @Schema(description = "Creation date", example = "2023-09-11T10:00:00Z")
    String createdAt,
    
    @Schema(description = "Last update date", example = "2023-09-11T10:00:00Z")
    String updatedAt
) {
}
