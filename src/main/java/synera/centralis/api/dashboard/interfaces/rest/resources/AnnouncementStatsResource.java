package synera.centralis.api.dashboard.interfaces.rest.resources;

import java.util.List;

/**
 * Resource for announcement statistics (pie chart data)
 */
public record AnnouncementStatsResource(
        String announcementId,
        String announcementTitle,
        long totalUsers,
        long totalViews,
        double viewPercentage,
        double notViewedPercentage,
        ViewStatsData viewStats
) {

    public record ViewStatsData(
            ViewStatItem viewed,
            ViewStatItem notViewed
    ) {}

    public record ViewStatItem(
            long count,
            double percentage,
            String color
    ) {}
}