package synera.centralis.api.company.domain.model.commands;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Command to create a new Space (room) within a company.
 */
public record CreateSpaceCommand(String name, String description, CompanyId companyId) {
    public CreateSpaceCommand {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Space name cannot be null or empty");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}
