package synera.centralis.api.profile.application.acl;

import org.springframework.stereotype.Service;

import synera.centralis.api.profile.domain.model.commands.CreateProfileCommand;
import synera.centralis.api.profile.domain.model.valueobjects.UserId;
import synera.centralis.api.profile.domain.services.ProfileCommandService;
import synera.centralis.api.profile.infrastructure.persistence.jpa.repositories.ProfileRepository;
import synera.centralis.api.profile.interfaces.acl.ProfileContextFacade;

import java.util.UUID;

/**
 * Profile Context Facade Implementation
 * Provides external access to Profile context operations
 */
@Service
public class ProfileContextFacadeImpl implements ProfileContextFacade {

    private final ProfileCommandService profileCommandService;
    private final ProfileRepository profileRepository;

    public ProfileContextFacadeImpl(ProfileCommandService profileCommandService, 
                                   ProfileRepository profileRepository) {
        this.profileCommandService = profileCommandService;
        this.profileRepository = profileRepository;
    }

    @Override
    public Long createBasicProfile(String userIdStr, String firstName, String lastName, String email, String url_image) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            
            // Create basic profile
            var command = new CreateProfileCommand(
                userId,
                firstName,
                lastName,
                email,
                url_image
            );

            var profile = profileCommandService.handle(command);
            return profile == null ? 0L : profile.getId().getMostSignificantBits();
        } catch (Exception e) {
            // Log error and return 0 to indicate failure
            System.err.println("Failed to create profile for user " + userIdStr + ": " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public boolean userHasProfile(String userIdStr) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            var userIdObj = new UserId(userId);
            return profileRepository.existsByUserId(userIdObj);
        } catch (Exception e) {
            System.err.println("Failed to check profile for user " + userIdStr + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public java.util.Optional<ProfileContextFacade.ProfileData> getProfileByUserId(String userIdStr) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            var userIdObj = new UserId(userId);
            var profile = profileRepository.findByUserId(userIdObj);
            
            if (profile.isPresent()) {
                var p = profile.get();
                return java.util.Optional.of(new ProfileContextFacade.ProfileData(
                    p.getFullName(),
                    p.getEmail(),
                    p.getAvatarUrl()
                ));
            }
            
            return java.util.Optional.empty();
        } catch (Exception e) {
            System.err.println("Failed to get profile for user " + userIdStr + ": " + e.getMessage());
            return java.util.Optional.empty();
        }
    }
}