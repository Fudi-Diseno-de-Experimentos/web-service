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
                        profile.email(), // Real email from Profile context
                        profile.department(), // Real department from Profile context
                        profile.position() // Real position from Profile context
                    ));
                }
            } else {
                // User exists in IAM but has no profile, return basic data
                String username = iamContextFacade.getUsernameById(userId.value());
                if (!username.isEmpty()) {
                    return Optional.of(new ExternalUserProfile(
                        userId.value(),
                        username,
                        username + "@empresa.com",
                        "Sin Departamento",
                        "Usuario"
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
     * Get users by department (for department breakdown)
     * @param department Department name
     * @return List of user profiles in the department
     */
    public List<ExternalUserProfile> getUsersByDepartment(String department) {
        try {
            // TODO: Implement when ProfileContextFacade has department queries
            // Return empty list for now as this should query Profile context, not IAM
            return List.of();
        } catch (Exception e) {
            // Fallback to mock users for each department
            return getMockUsersByDepartment(department);
        }
    }

    /**
     * Get department statistics (for pie chart breakdowns)
     * @return Map of department name to user count
     */
    public Map<String, Long> getDepartmentStatistics() {
        try {
            // TODO: Implement when ProfileContextFacade has department statistics
            // Return mock data for now as this should query Profile context, not IAM
            return getMockDepartmentStatistics();
        } catch (Exception e) {
            // Fallback department distribution
            return getMockDepartmentStatistics();
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
            "user." + userId.value().toString().substring(0, 8) + "@empresa.com",
            "Tecnología", // Mock department
            "Desarrollador" // Mock position
        ));
    }

    private List<ExternalUserProfile> getMockUsersByDepartment(String department) {
        return List.of(
            new ExternalUserProfile(UUID.randomUUID(), "User 1", "user1@empresa.com", department, "Employee"),
            new ExternalUserProfile(UUID.randomUUID(), "User 2", "user2@empresa.com", department, "Manager")
        );
    }

    private Map<String, Long> getMockDepartmentStatistics() {
        return Map.of(
            "Recursos Humanos", 25L,
            "Finanzas", 30L,
            "Tecnología", 40L,
            "Marketing", 35L,
            "Operaciones", 20L
        );
    }

    private List<ExternalUserProfile> getMockTopUsers(int limit) {
        return List.of(
            new ExternalUserProfile(
                UUID.randomUUID(),
                "Juan Pérez",
                "juan.perez@empresa.com",
                "Recursos Humanos",
                "Especialista RRHH"
            ),
            new ExternalUserProfile(
                UUID.randomUUID(),
                "María García",
                "maria.garcia@empresa.com",
                "Finanzas",
                "Analista Financiero"
            ),
            new ExternalUserProfile(
                UUID.randomUUID(),
                "Carlos López",
                "carlos.lopez@empresa.com",
                "Tecnología",
                "Desarrollador Senior"
            )
        ).subList(0, Math.min(limit, 3));
    }
}