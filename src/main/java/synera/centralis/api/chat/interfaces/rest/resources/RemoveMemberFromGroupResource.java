package synera.centralis.api.chat.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Resource for removing a member from a group.
 * Contains the user ID of the member to be removed.
 */
public record RemoveMemberFromGroupResource(
        @NotNull(message = "User ID is required")
        UUID userId
) {
}