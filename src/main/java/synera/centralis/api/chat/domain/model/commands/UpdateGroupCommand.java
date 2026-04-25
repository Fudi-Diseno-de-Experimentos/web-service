package synera.centralis.api.chat.domain.model.commands;

import java.util.UUID;

/**
 * Command to update group information (name, description, image).
 * Allows updating multiple fields in a single operation.
 */
public record UpdateGroupCommand(
        UUID groupId,
        String name,
        String description,
        String imageUrl
) {
    public UpdateGroupCommand {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (name != null) {
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Group name cannot be empty");
            }
            if (name.length() > 100) {
                throw new IllegalArgumentException("Group name cannot exceed 100 characters");
            }
        }
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Group description cannot exceed 500 characters");
        }
        if (imageUrl != null && imageUrl.length() > 500) {
            throw new IllegalArgumentException("Image URL cannot exceed 500 characters");
        }
    }
}