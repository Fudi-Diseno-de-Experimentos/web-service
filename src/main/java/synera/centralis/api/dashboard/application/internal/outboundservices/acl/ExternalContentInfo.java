package synera.centralis.api.dashboard.application.internal.outboundservices.acl;

import java.util.UUID;

/**
 * External content data for ACL enrichment (announcements and events)
 */
public record ExternalContentInfo(
    UUID contentId,
    String title,
    String description,
    String location, // For events
    java.time.LocalDateTime date // For events
) {
    public ExternalContentInfo {
        if (contentId == null) {
            throw new IllegalArgumentException("Content ID cannot be null");
        }
        // Set defaults for null values
        title = title != null ? title : "Unknown Content";
        description = description != null ? description : "No description available";
        location = location != null ? location : ""; // Empty for announcements
    }
}