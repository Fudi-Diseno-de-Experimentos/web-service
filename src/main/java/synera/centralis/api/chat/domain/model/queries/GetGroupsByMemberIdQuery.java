package synera.centralis.api.chat.domain.model.queries;

import synera.centralis.api.chat.domain.model.valueobjects.UserId;

/**
 * Query to retrieve all groups where a user is a member.
 */
public record GetGroupsByMemberIdQuery(
        UserId memberId
) {
    public GetGroupsByMemberIdQuery {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
    }
}