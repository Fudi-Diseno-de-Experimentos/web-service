package synera.centralis.api.dashboard.interfaces.rest.resources;

import java.util.List;

/**
 * Resource for event statistics (pie chart data)
 */
public record EventStatsResource(
        String eventId,
        String eventTitle,
        long totalUsers,
        long totalViews,
        double viewPercentage,
        double notViewedPercentage,
        ViewStatsData viewStats,
        List<DepartmentBreakdownData> departmentBreakdown
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

    public record DepartmentBreakdownData(
            String department,
            long totalUsers,
            long viewedUsers,
            double percentage
    ) {}
}