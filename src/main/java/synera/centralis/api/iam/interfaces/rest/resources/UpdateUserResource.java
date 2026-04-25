package synera.centralis.api.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Update user resource for IAM context
 * Only handles password updates - profile data is managed by Profile context
 */
@Schema(description = "Request to update user password")
public record UpdateUserResource(
    @Schema(description = "New password", example = "newSecurePassword123", required = true)
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    String newPassword
) {
}
