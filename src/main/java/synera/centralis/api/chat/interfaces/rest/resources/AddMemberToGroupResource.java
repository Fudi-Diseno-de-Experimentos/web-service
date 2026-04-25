package synera.centralis.api.chat.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Resource for adding a member to a group.
 * Contains the user ID of the member to be added.
 */
public record AddMemberToGroupResource(
        @NotNull(message = "User ID is required")
        UUID userId
) {
}