package synera.centralis.api.profile.domain.model.queries;

import java.util.UUID;

/**
 * Get profile by ID query
 * Query to retrieve a profile by its ID
 */
public record GetProfileByIdQuery(UUID profileId) {
    public GetProfileByIdQuery {
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }
    }
}