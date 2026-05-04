package synera.centralis.api.chat.domain.model.commands;

import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.UUID;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Command to remove a member from a group.
 * Contains the group ID and the user to be removed.
 */
public record RemoveMemberFromGroupCommand(
        UUID groupId,
        UserId memberToRemove,
        CompanyId companyId
) {
    public RemoveMemberFromGroupCommand {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (memberToRemove == null) {
            throw new IllegalArgumentException("Member to remove cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}