package synera.centralis.api.profile.interfaces.rest.resources;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Profile resource
 * Response resource for profile information
 */
@Schema(description = "Profile information")
public record ProfileResource(
    @Schema(description = "Profile ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID profileId,

    @Schema(description = "User ID from IAM context", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID userId,

    @Schema(description = "First name", example = "John")
    String firstName,

    @Schema(description = "Last name", example = "Doe")
    String lastName,

    @Schema(description = "Email", example = "john.doe@company.com")
    String email,

    @Schema(description = "Full name", example = "John Doe")
    String fullName,

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    String avatarUrl,
    
    @Schema(description = "Company ID", example = "123e4567-e89b-12d3-a456-426614174001")
    UUID companyId
) {
}