package synera.centralis.api.profile.interfaces.acl;

/**
 * Profile Context Facade
 * Interface for accessing Profile context from external contexts
 */
public interface ProfileContextFacade {
    
    /**
     * Create a basic profile for a new user
     * @param userIdStr the user ID from IAM context as string (UUID)
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email address
     * @return the profile ID if created successfully, 0L if failed
     */
    Long createBasicProfile(String userIdStr, String firstName, String lastName, String email);
    
    /**
     * Check if user has a profile
     * @param userIdStr the user ID from IAM context as string (UUID)
     * @return true if user has a profile
     */
    boolean userHasProfile(String userIdStr);
    
    /**
     * Get profile information by user ID
     * @param userIdStr the user ID from IAM context as string (UUID)
     * @return Optional with profile data (firstName, lastName, email, department, position)
     */
    java.util.Optional<ProfileData> getProfileByUserId(String userIdStr);
    
    /**
     * Profile data record for external access
     */
    record ProfileData(
        String firstName,
        String lastName,
        String email,
        String department,
        String position
    ) {}
}