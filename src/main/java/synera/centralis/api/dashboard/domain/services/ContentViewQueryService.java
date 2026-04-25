package synera.centralis.api.dashboard.domain.services;

import synera.centralis.api.dashboard.domain.model.aggregates.ContentView;
import synera.centralis.api.dashboard.domain.model.queries.*;
import synera.centralis.api.dashboard.interfaces.rest.resources.*;

import java.util.List;

/**
 * Query service interface for content view read operations
 */
public interface ContentViewQueryService {

    /**
     * Get all announcements viewed by a specific user
     * @param query GetUserAnnouncementViewsQuery
     * @return List of UserAnnouncementViewResource
     */
    List<UserAnnouncementViewResource> handle(GetUserAnnouncementViewsQuery query);

    /**
     * Get all users who viewed a specific announcement
     * @param query GetAnnouncementViewersQuery
     * @return List of AnnouncementViewerResource
     */
    List<AnnouncementViewerResource> handle(GetAnnouncementViewersQuery query);

    /**
     * Get all events viewed by a specific user
     * @param query GetUserEventViewsQuery  
     * @return List of UserEventViewResource
     */
    List<UserEventViewResource> handle(GetUserEventViewsQuery query);

    /**
     * Get all users who viewed a specific event
     * @param query GetEventViewersQuery
     * @return List of EventViewerResource
     */
    List<EventViewerResource> handle(GetEventViewersQuery query);

    /**
     * Get summary of all views for dashboard overview
     * @param query GetViewsSummaryQuery
     * @return ViewsSummaryResource
     */
    ViewsSummaryResource handle(GetViewsSummaryQuery query);

    /**
     * Get announcement statistics for pie chart
     * @param query GetAnnouncementStatsQuery
     * @return AnnouncementStatsResource
     */
    AnnouncementStatsResource handle(GetAnnouncementStatsQuery query);

    /**
     * Get event statistics for pie chart
     * @param query GetEventStatsQuery
     * @return EventStatsResource
     */
    EventStatsResource handle(GetEventStatsQuery query);

    // Internal methods that return domain objects
    /**
     * Get ContentViews by user and content type (internal use)
     */
    List<ContentView> getContentViewsByUser(GetUserAnnouncementViewsQuery query);
    List<ContentView> getContentViewsByUser(GetUserEventViewsQuery query);
    
    /**
     * Get ContentViews by content and type (internal use)
     */
    List<ContentView> getContentViewsByContent(GetAnnouncementViewersQuery query);
    List<ContentView> getContentViewsByContent(GetEventViewersQuery query);
}