package synera.centralis.api.iam.domain.model.commands;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import java.util.UUID;

/**
 * Assign user to company command
 * <p>
 *     This class represents the command to assign an existing user to a company.
 * </p>
 * @param userId the ID of the user to assign
 * @param companyId the ID of the company
 */
public record AssignUserToCompanyCommand(UUID userId, CompanyId companyId) {
    public AssignUserToCompanyCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}
