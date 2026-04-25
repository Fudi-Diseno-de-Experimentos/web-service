package synera.centralis.api.chat.domain.model.commands;

import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.UUID;

/**
 * Command to add a new member to a group.
 * Contains the group ID and the user to be added.
 */
public record AddMemberToGroupCommand(
        UUID groupId,
        UserId memberToAdd
) {
    public AddMemberToGroupCommand {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (memberToAdd == null) {
            throw new IllegalArgumentException("Member to add cannot be null");
        }
    }
}