package synera.centralis.api.profile.domain.services;

import java.util.Optional;

import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.domain.model.commands.CreateProfileCommand;
import synera.centralis.api.profile.domain.model.commands.UpdateProfileCommand;

/**
 * Profile command service
 * Defines the contract for profile command operations
 */
public interface ProfileCommandService {
    
    /**
     * Handle create profile command
     * @param command the create profile command
     * @return the created profile
     */
    Optional<Profile> handle(CreateProfileCommand command);
    
    /**
     * Handle update profile command
     * @param command the update profile command
     * @return the updated profile
     */
    Optional<Profile> handle(UpdateProfileCommand command);
}