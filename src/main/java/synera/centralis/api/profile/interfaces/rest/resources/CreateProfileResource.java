package synera.centralis.api.profile.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import synera.centralis.api.profile.domain.model.valueobjects.Department;
import synera.centralis.api.profile.domain.model.valueobjects.Position;
import java.util.UUID;

/**
 * Create profile resource
 * Request resource for creating a new profile
 */
@Schema(description = "Request to create a new profile")
public record CreateProfileResource(
    @Schema(description = "User ID from IAM context", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    @NotNull(message = "User ID cannot be null")
    UUID userId,

    @Schema(description = "First name", example = "John", required = true)
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    String firstName,

    @Schema(description = "Last name", example = "Doe", required = true)
    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    String lastName,

    @Schema(description = "Email", example = "john.doe@company.com", required = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    String email,

    @Schema(description = "Avatar URL", example = "https://i.pinimg.com/736x/27/04/39/2704399f46a1ac9a1d353e59a91dfe19.jpg")
    @Size(max = 255, message = "Avatar URL must not exceed 255 characters")
    String avatarUrl,

    @Schema(description = "Position", example = "EMPLOYEE", required = true)
    @NotNull(message = "Position cannot be null")
    Position position,

    @Schema(description = "Department", example = "OTHER", required = true)
    @NotNull(message = "Department cannot be null")
    Department department
) {
}