package synera.centralis.api.chat.domain.model.commands;

import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;

import java.util.UUID;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Command to update group visibility (PUBLIC/PRIVATE).
 * Separated from general group updates due to different security implications.
 */
public record UpdateGroupVisibilityCommand(
        UUID groupId,
        GroupVisibility visibility,
        CompanyId companyId
) {
    public UpdateGroupVisibilityCommand {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (visibility == null) {
            throw new IllegalArgumentException("Group visibility cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}