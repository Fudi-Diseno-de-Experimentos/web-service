package synera.centralis.api.dashboard.application.internal.outboundservices.acl;

import java.util.UUID;

/**
 * User profile data for ACL enrichment
 */
public record ExternalUserProfile(
    UUID userId,
    String fullName,
    String email,
    String department,
    String position
) {
    public ExternalUserProfile {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        // Set defaults for null values
        fullName = fullName != null ? fullName : "Unknown User";
        email = email != null ? email : "unknown@company.com";
        department = department != null ? department : "Unknown Department";
        position = position != null ? position : "Unknown Position";
    }
}