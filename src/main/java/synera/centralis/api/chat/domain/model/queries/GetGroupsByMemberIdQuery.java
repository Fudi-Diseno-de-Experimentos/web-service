package synera.centralis.api.chat.domain.model.queries;

import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Query to retrieve all groups where a user is a member.
 */
public record GetGroupsByMemberIdQuery(
        UserId memberId,
        CompanyId companyId
) {
    public GetGroupsByMemberIdQuery {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}