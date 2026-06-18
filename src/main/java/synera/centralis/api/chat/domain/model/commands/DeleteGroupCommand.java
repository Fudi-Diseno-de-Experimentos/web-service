package synera.centralis.api.chat.domain.model.commands;

import java.util.UUID;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Command to delete a group from the chat system.
 * This will also delete all associated messages.
 */
public record DeleteGroupCommand(
        UUID groupId, CompanyId companyId
) {
    public DeleteGroupCommand {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}