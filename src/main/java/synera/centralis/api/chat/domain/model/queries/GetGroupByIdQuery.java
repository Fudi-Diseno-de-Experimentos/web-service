package synera.centralis.api.chat.domain.model.queries;

import java.util.UUID;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to retrieve a specific group by its ID.
 */
public record GetGroupByIdQuery(
        UUID groupId,
        CompanyId companyId
) {
    public GetGroupByIdQuery {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}