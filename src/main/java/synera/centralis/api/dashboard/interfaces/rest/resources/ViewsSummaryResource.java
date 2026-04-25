package synera.centralis.api.dashboard.interfaces.rest.resources;

import java.util.List;

/**
 * Resource for dashboard views summary
 */
public record ViewsSummaryResource(
        long totalAnnouncementViews,
        long totalEventViews,
        long totalUniqueUsers,
        MostViewedContentResource mostViewedAnnouncement,
        MostViewedContentResource mostViewedEvent,
        List<TopActiveUserResource> topActiveUsers
) {

    public record MostViewedContentResource(
            String announcementId,
            String title,
            long viewCount
    ) {}

    public record TopActiveUserResource(
            String userId,
            String userFullName,
            long totalViews
    ) {}
}