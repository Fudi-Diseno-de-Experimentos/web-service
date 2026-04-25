package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Resource for adding a member to a group.
 * Contains the user ID to be added as a member.
 */
@Schema(description = "Request to add a member to a group")
public record AddMemberResource(
        @Schema(description = "User ID to add as a member", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Member ID is required")
        UUID memberId
) {
}