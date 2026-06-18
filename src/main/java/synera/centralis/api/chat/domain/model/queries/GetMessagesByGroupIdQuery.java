package synera.centralis.api.chat.domain.model.queries;

import java.util.UUID;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to retrieve all messages from a specific group.
 * Messages should be ordered by creation time.
 */
public record GetMessagesByGroupIdQuery(
        UUID groupId,
        CompanyId companyId
) {
    public GetMessagesByGroupIdQuery {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}