package synera.centralis.api.iam.application.internal.outboundservices.acl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import synera.centralis.api.profile.interfaces.acl.ProfileContextFacade;

/**
 * External Profile Service
 * ACL implementation for accessing Profile context from IAM context
 */
@Service
public class ExternalProfileService {

    private final ProfileContextFacade profileContextFacade;

    public ExternalProfileService(ProfileContextFacade profileContextFacade) {
        this.profileContextFacade = profileContextFacade;
    }

    /**
     * Create a basic profile for a new user
     * @param userIdStr the user ID from IAM context as string
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email address
     * @return Optional profile ID if created successfully
     */
    public Optional<Long> createBasicProfile(String userIdStr, String firstName, String lastName, String email) {
        var profileId = profileContextFacade.createBasicProfile(userIdStr, firstName, lastName, email);
        return profileId == 0L ? Optional.empty() : Optional.of(profileId);
    }

    /**
     * Check if user has a profile
     * @param userIdStr the user ID from IAM context as string
     * @return true if user has a profile
     */
    public boolean userHasProfile(String userIdStr) {
        return profileContextFacade.userHasProfile(userIdStr);
    }
}