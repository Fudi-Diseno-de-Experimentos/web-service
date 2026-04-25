package synera.centralis.api.profile.domain.services;

import java.util.List;
import java.util.Optional;

import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.domain.model.queries.GetAllProfilesQuery;
import synera.centralis.api.profile.domain.model.queries.GetProfileByIdQuery;
import synera.centralis.api.profile.domain.model.queries.GetProfileByUserIdQuery;

/**
 * Profile query service
 * Defines the contract for profile query operations
 */
public interface ProfileQueryService {
    
    /**
     * Handle get profile by ID query
     * @param query the get profile by ID query
     * @return the profile if found
     */
    Optional<Profile> handle(GetProfileByIdQuery query);
    
    /**
     * Handle get profile by user ID query
     * @param query the get profile by user ID query
     * @return the profile if found
     */
    Optional<Profile> handle(GetProfileByUserIdQuery query);
    
    /**
     * Handle get all profiles query
     * @param query the get all profiles query
     * @return list of all profiles
     */
    List<Profile> handle(GetAllProfilesQuery query);
}