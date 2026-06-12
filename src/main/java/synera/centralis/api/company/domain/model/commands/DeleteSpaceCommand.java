package synera.centralis.api.company.domain.model.commands;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.UUID;

/**
 * Command to delete a Space. Blocked while the space has future bookings.
 */
public record DeleteSpaceCommand(UUID spaceId, CompanyId companyId) {
    public DeleteSpaceCommand {
        if (spaceId == null) {
            throw new IllegalArgumentException("Space ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}
