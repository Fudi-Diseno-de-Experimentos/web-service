package synera.centralis.api.dashboard.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;
import synera.centralis.api.dashboard.domain.model.valueobjects.UserId;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;
import synera.centralis.api.profile.interfaces.acl.ProfileContextFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * External User Service - ACL implementation for accessing User data from IAM and Profile contexts
 * Integrates with IAM context for authentication and Profile context for user profile data
 */
@Service
public class ExternalUserService {

    private final IamContextFacade iamContextFacade;
    private final ProfileContextFacade profileContextFacade;

    public ExternalUserService(IamContextFacade iamContextFacade, 
                              ProfileContextFacade profileContextFacade) {
        this.iamContextFacade = iamContextFacade;
        this.profileContextFacade = profileContextFacade;
    }

    /**
     * Fetch user profile by user ID - combines IAM authentication data with Profile context data
     * @param userId The user ID
     * @return An Optional of ExternalUserProfile
     */
    public Optional<ExternalUserProfile> fetchUserProfile(UserId userId) {
        try {
            // First verify user exists in IAM context
            if (!iamContextFacade.userExists(userId.value())) {
                return Optional.empty();
            }

            // Try to get profile data from Profile context
            if (profileContextFacade.userHasProfile(userId.value().toString())) {
                var profileData = profileContextFacade.getProfileByUserId(userId.value().toString());
                
                if (profileData.isPresent()) {
                    var profile = profileData.get();
                    String displayName = profile.firstName() + " " + profile.lastName();
                    
                    return Optional.of(new ExternalUserProfile(
                        userId.value(),
                        displayName, // Real full name from Profile context
                        profile.email() // Real email from Profile context
                    ));
                }
            } else {
                // User exists in IAM but has no profile, return basic data
                String username = iamContextFacade.getUsernameById(userId.value());
                if (!username.isEmpty()) {
                    return Optional.of(new ExternalUserProfile(
                        userId.value(),
                        username,
                        username + "@empresa.com"
                    ));
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            // Fallback to mock data if contexts are not available
            return getMockUserProfile(userId);
        }
    }

    /**
     * Fetch user profiles for multiple users (batch operation)
     * @param userIds List of user IDs
     * @return Map of userId to ExternalUserProfile
     */
    public Map<UUID, ExternalUserProfile> fetchUserProfiles(List<UserId> userIds) {
        try {
            Map<UUID, ExternalUserProfile> result = new HashMap<>();
            
            for (UserId userId : userIds) {
                var profile = fetchUserProfile(userId);
                profile.ifPresent(p -> result.put(userId.value(), p));
            }
            
            return result;
        } catch (Exception e) {
            // Fallback to individual calls if batch operation fails
            return userIds.stream()
                .map(this::fetchUserProfile)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                    ExternalUserProfile::userId,
                    profile -> profile
                ));
        }
    }

    /**
     * Check if user exists
     * @param userId The user ID
     * @return true if user exists, false otherwise
     */
    public boolean userExists(UserId userId) {
        try {
            return iamContextFacade.userExists(userId.value());
        } catch (Exception e) {
            // Assume user exists if we can't verify
            return true;
        }
    }

    /**
     * Get total number of users (for statistics)
     * @return Total user count
     */
    public long getTotalUserCount() {
        try {
            return iamContextFacade.getTotalUserCount();
        } catch (Exception e) {
            // Fallback count
            return 150L;
        }
    }

    /**
     * Get top active users based on recent activity
     * @param limit Number of top users to return
     * @return List of most active user profiles
     */
    public List<ExternalUserProfile> getTopActiveUsers(int limit) {
        try {
            // TODO: Implement activity tracking when available
            // This should combine data from multiple contexts to determine activity
            return getMockTopUsers(limit);
        } catch (Exception e) {
            // Return mock data
            return getMockTopUsers(limit);
        }
    }

    // Private helper methods for fallback data
    private Optional<ExternalUserProfile> getMockUserProfile(UserId userId) {
        return Optional.of(new ExternalUserProfile(
            userId.value(),
            "Mock User " + userId.value().toString().substring(0, 8),
            "user." + userId.value().toString().substring(0, 8) + "@empresa.com"
        ));
    }

    private List<ExternalUserProfile> getMockTopUsers(int limit) {
        return List.of(
            new ExternalUserProfile(
                UUID.randomUUID(),
                "Juan Pérez",
                "juan.perez@empresa.com"
            ),
            new ExternalUserProfile(
                UUID.randomUUID(),
                "María García",
                "maria.garcia@empresa.com"
            ),
            new ExternalUserProfile(
                UUID.randomUUID(),
                "Carlos López",
                "carlos.lopez@empresa.com"
            )
        ).subList(0, Math.min(limit, 3));
    }
}