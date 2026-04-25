package synera.centralis.api.profile.application.internal.commandservices;

import java.util.Optional;

import org.springframework.stereotype.Service;

import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.domain.model.commands.CreateProfileCommand;
import synera.centralis.api.profile.domain.model.commands.UpdateProfileCommand;
import synera.centralis.api.profile.domain.model.valueobjects.UserId;
import synera.centralis.api.profile.domain.services.ProfileCommandService;
import synera.centralis.api.profile.infrastructure.persistence.jpa.repositories.ProfileRepository;

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
    public Optional<Profile> handle(CreateProfileCommand command) {
        // Validate that user doesn't already have a profile
        var userId = new UserId(command.userId());
        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("User already has a profile");
        }

        // Create new profile
        var profile = Profile.createProfile(
            command.userId(),
            command.firstName(),
            command.lastName(),
            command.email(),
            command.avatarUrl(),
            command.position(),
            command.department()
        );

        // Save and return
        var savedProfile = profileRepository.save(profile);
        return Optional.of(savedProfile);
    }

    @Override
    public Optional<Profile> handle(UpdateProfileCommand command) {
        // Find existing profile
        var existingProfile = profileRepository.findById(command.profileId());
        if (existingProfile.isEmpty()) {
            throw new IllegalArgumentException("Profile not found");
        }

        // Update profile
        var profile = existingProfile.get();
        profile.updateProfile(
            command.firstName(),
            command.lastName(),
            command.email(),
            command.avatarUrl(),
            command.position(),
            command.department()
        );

        // Save and return
        var savedProfile = profileRepository.save(profile);
        return Optional.of(savedProfile);
    }
}