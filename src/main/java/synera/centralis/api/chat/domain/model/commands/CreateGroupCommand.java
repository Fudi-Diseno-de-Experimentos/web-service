package synera.centralis.api.chat.domain.model.commands;

import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.UUID;

/**
 * Command to create a new group in the chat system.
 * Contains all necessary information to create a group.
 */
public record CreateGroupCommand(
        String name,
        String description,
        String imageUrl,
        GroupVisibility visibility,
        List<UUID> memberIds,
        UserId createdBy
) {
    public CreateGroupCommand {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Group name cannot exceed 100 characters");
        }
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Group description cannot exceed 500 characters");
        }
        if (imageUrl != null && imageUrl.length() > 500) {
            throw new IllegalArgumentException("Image URL cannot exceed 500 characters");
        }
        if (visibility == null) {
            throw new IllegalArgumentException("Group visibility cannot be null");
        }
        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("Group must have at least one member");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("Created by cannot be null");
        }
    }
}