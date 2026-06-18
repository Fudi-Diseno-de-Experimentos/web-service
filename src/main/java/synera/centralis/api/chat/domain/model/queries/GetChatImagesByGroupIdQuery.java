package synera.centralis.api.chat.domain.model.queries;

import java.util.UUID;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to get all chat images by group ID.
 */
public record GetChatImagesByGroupIdQuery(UUID groupId, CompanyId companyId) {
    public GetChatImagesByGroupIdQuery {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}