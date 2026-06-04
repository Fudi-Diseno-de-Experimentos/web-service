package synera.centralis.api.profile.domain.services;

import java.util.List;
import java.util.Optional;

import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.domain.model.queries.*;

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

    /**
     * Handle get profiles by company ID query
     * @param query the query containing the company ID
     * @return list of profiles for that company
     */
    List<Profile> handle(GetProfilesByCompanyIdQuery query);

    /**
     * Handle get profiles without company query
     * @param query the query
     * @return list of profiles without company
     */
    List<Profile> handle(GetProfilesWithoutCompanyQuery query);
}