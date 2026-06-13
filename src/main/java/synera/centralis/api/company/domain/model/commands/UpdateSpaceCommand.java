package synera.centralis.api.company.domain.model.commands;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.UUID;

/**
 * Command to update an existing Space. Null name/description fields are left unchanged.
 */
public record UpdateSpaceCommand(UUID spaceId, String name, String description, CompanyId companyId) {
    public UpdateSpaceCommand {
        if (spaceId == null) {
            throw new IllegalArgumentException("Space ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}
