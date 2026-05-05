package synera.centralis.api.profile.application.internal.commandservices;

import java.util.Optional;

import org.springframework.stereotype.Service;

import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.domain.model.commands.CreateProfileCommand;
import synera.centralis.api.profile.domain.model.commands.UpdateProfileCommand;
import synera.centralis.api.profile.domain.model.valueobjects.UserId;
import synera.centralis.api.profile.domain.services.ProfileCommandService;
import synera.centralis.api.profile.infrastructure.persistence.jpa.repositories.ProfileRepository;
import synera.centralis.api.shared.domain.exceptions.DuplicateResourceException;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;

/**
 * Profile command service implementation
 * Handles profile command operations
 */
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile handle(CreateProfileCommand command) {
        // Validate that user doesn't already have a profile
        var userId = new UserId(command.userId());
        if (profileRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException("User already has a profile");
        }

        // Create new profile
        var profile = Profile.createProfile(
            command.userId(),
            command.firstName(),
            command.lastName(),
            command.email(),
            command.avatarUrl()
        );

        // Save and return
        return profileRepository.save(profile);
    }

    @Override
    public Profile handle(UpdateProfileCommand command) {
        // Find existing profile
        var profile = profileRepository.findById(command.profileId())
            .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        // Update profile
        profile.updateProfile(
            command.firstName(),
            command.lastName(),
            command.email(),
            command.avatarUrl()
        );

        // Save and return
        return profileRepository.save(profile);
    }
}