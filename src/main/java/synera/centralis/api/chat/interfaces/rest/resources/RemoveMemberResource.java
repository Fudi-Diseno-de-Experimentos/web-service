package synera.centralis.api.chat.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Resource for removing a member from a group.
 * Contains the user ID to be removed from the group.
 */
@Schema(description = "Request to remove a member from a group")
public record RemoveMemberResource(
        @Schema(description = "User ID to remove from the group", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Member ID is required")
        UUID memberId
) {
}