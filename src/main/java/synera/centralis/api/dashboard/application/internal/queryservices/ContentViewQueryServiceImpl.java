package synera.centralis.api.dashboard.application.internal.queryservices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.dashboard.application.internal.outboundservices.acl.ExternalContentInfo;
import synera.centralis.api.dashboard.application.internal.outboundservices.acl.ExternalContentService;
import synera.centralis.api.dashboard.application.internal.outboundservices.acl.ExternalUserProfile;
import synera.centralis.api.dashboard.application.internal.outboundservices.acl.ExternalUserService;
import synera.centralis.api.dashboard.domain.model.aggregates.ContentView;
import synera.centralis.api.dashboard.domain.model.queries.*;
import synera.centralis.api.dashboard.domain.model.valueobjects.AnnouncementId;
import synera.centralis.api.dashboard.domain.model.valueobjects.ContentType;
import synera.centralis.api.dashboard.domain.model.valueobjects.EventId;
import synera.centralis.api.dashboard.domain.services.ContentViewQueryService;
import synera.centralis.api.dashboard.infrastructure.persistence.jpa.repositories.ContentViewRepository;
import synera.centralis.api.dashboard.interfaces.rest.resources.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ContentViewQueryService  
 * Handles read-only query operations for content views
 */
@Service
public class ContentViewQueryServiceImpl implements ContentViewQueryService {

    private final ContentViewRepository contentViewRepository;
    private final ExternalUserService externalUserService;
    private final ExternalContentService externalContentService;

    public ContentViewQueryServiceImpl(ContentViewRepository contentViewRepository,
                                     ExternalUserService externalUserService,
                                     ExternalContentService externalContentService) {
        this.contentViewRepository = contentViewRepository;
        this.externalUserService = externalUserService;
        this.externalContentService = externalContentService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAnnouncementViewResource> handle(GetUserAnnouncementViewsQuery query) {
        List<ContentView> contentViews = getContentViewsByUser(query);
        return contentViews.stream()
            .map(this::toUserAnnouncementViewResource)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementViewerResource> handle(GetAnnouncementViewersQuery query) {
        List<ContentView> contentViews = getContentViewsByContent(query);
        return contentViews.stream()
            .map(this::toAnnouncementViewerResource)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEventViewResource> handle(GetUserEventViewsQuery query) {
        List<ContentView> contentViews = getContentViewsByUser(query);
        return contentViews.stream()
            .map(this::toUserEventViewResource)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventViewerResource> handle(GetEventViewersQuery query) {
        List<ContentView> contentViews = getContentViewsByContent(query);
        return contentViews.stream()
            .map(this::toEventViewerResource)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ViewsSummaryResource handle(GetViewsSummaryQuery query) {
        // Get all content views
        List<ContentView> allViews = contentViewRepository.findAllByOrderByViewDateTimeDesc();
        
        // Calculate summary statistics
        long totalAnnouncementViews = allViews.stream()
            .filter(view -> view.getContentType() == ContentType.ANNOUNCEMENT)
            .count();
            
        long totalEventViews = allViews.stream()
            .filter(view -> view.getContentType() == ContentType.EVENT)
            .count();
            
        long totalUniqueUsers = allViews.stream()
            .map(view -> view.getUserId().value())
            .distinct()
            .count();

        // Get most viewed content info
        var mostViewedAnnouncementInfo = externalContentService.getMostViewedAnnouncementInfo()
            .map(info -> new ViewsSummaryResource.MostViewedContentResource(
                info.contentId().toString(),
                info.title(),
                67L // TODO: Get actual view count from analytics
            ));

        var mostViewedEventInfo = externalContentService.getMostViewedEventInfo()
            .map(info -> new ViewsSummaryResource.MostViewedContentResource(
                info.contentId().toString(),
                info.title(),
                78L // TODO: Get actual view count from analytics
            ));

        // Get top active users
        var topActiveUsers = externalUserService.getTopActiveUsers(5)
            .stream()
            .map(user -> new ViewsSummaryResource.TopActiveUserResource(
                user.userId().toString(),
                user.fullName(),
                15L // TODO: Get actual view count for user
            ))
            .toList();

        return new ViewsSummaryResource(
            totalAnnouncementViews,
            totalEventViews,
            totalUniqueUsers,
            mostViewedAnnouncementInfo.orElse(null),
            mostViewedEventInfo.orElse(null),
            topActiveUsers
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementStatsResource handle(GetAnnouncementStatsQuery query) {
        List<ContentView> views = getContentViewsByContent(new GetAnnouncementViewersQuery(query.announcementId()));
        
        // Get basic stats
        long totalViews = views.size();
        long totalUsers = externalUserService.getTotalUserCount();
        
        // Get announcement info
        var announcementInfo = externalContentService.fetchAnnouncementInfo(query.announcementId())
            .orElse(new ExternalContentInfo(
                query.announcementId().value(),
                "Unknown Announcement", 
                "Content not found", 
                null, 
                null
            ));

        // Get department breakdown
        var departmentStats = externalUserService.getDepartmentStatistics();
        var departmentBreakdown = departmentStats.entrySet().stream()
            .map(entry -> {
                // Count views from users in this department
                long departmentViews = views.stream()
                    .map(view -> externalUserService.fetchUserProfile(view.getUserId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(user -> entry.getKey().equals(user.department()))
                    .count();
                
                return new AnnouncementStatsResource.DepartmentBreakdownData(
                    entry.getKey(),
                    entry.getValue(),
                    departmentViews,
                    (departmentViews * 100.0) / entry.getValue()
                );
            })
            .toList();

        // Calculate percentages
        double viewPercentage = (totalViews * 100.0) / totalUsers;
        double notViewedPercentage = ((totalUsers - totalViews) * 100.0) / totalUsers;

        // Create view stats
        var viewStats = new AnnouncementStatsResource.ViewStatsData(
            new AnnouncementStatsResource.ViewStatItem(totalViews, viewPercentage, "#4CAF50"),
            new AnnouncementStatsResource.ViewStatItem(totalUsers - totalViews, notViewedPercentage, "#FF9800")
        );

        return new AnnouncementStatsResource(
            query.announcementId().toString(),
            announcementInfo.title(),
            totalUsers,
            totalViews,
            viewPercentage,
            notViewedPercentage,
            viewStats,
            departmentBreakdown
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EventStatsResource handle(GetEventStatsQuery query) {
        List<ContentView> views = getContentViewsByContent(new GetEventViewersQuery(query.eventId()));
        
        // Get basic stats
        long totalViews = views.size();
        
        // Get event participation stats to get only users registered for this specific event
        var participationStats = externalContentService.fetchEventParticipationStats(query.eventId());
        long totalUsers = participationStats.getOrDefault("registered", 0L);
        
        // Get event info
        var eventInfo = externalContentService.fetchEventInfo(query.eventId())
            .orElse(new ExternalContentInfo(
                query.eventId().value(),
                "Unknown Event", 
                "Content not found", 
                "Unknown Location",
                LocalDateTime.now()
            ));

        // Get department breakdown
        // Note: Currently using all departments in the system, but ideally should use only departments
        // of users registered for this specific event. This requires getting the full list of registered users
        // from the Event context, which would be a future enhancement.
        var departmentStats = externalUserService.getDepartmentStatistics();
        var departmentBreakdown = departmentStats.entrySet().stream()
            .map(entry -> {
                // Count views from users in this department
                long departmentViews = views.stream()
                    .map(view -> externalUserService.fetchUserProfile(view.getUserId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(user -> entry.getKey().equals(user.department()))
                    .count();
                
                // TODO: Use actual registered users count per department for this event instead of total department size
                long departmentTotal = entry.getValue();
                double percentage = departmentTotal > 0 ? (departmentViews * 100.0) / departmentTotal : 0.0;
                
                return new EventStatsResource.DepartmentBreakdownData(
                    entry.getKey(),
                    departmentTotal,
                    departmentViews,
                    percentage
                );
            })
            .toList();

        // Calculate percentages (avoid division by zero)
        double viewPercentage = totalUsers > 0 ? (totalViews * 100.0) / totalUsers : 0.0;
        double notViewedPercentage = totalUsers > 0 ? ((totalUsers - totalViews) * 100.0) / totalUsers : 0.0;

        // Create view stats
        var viewStats = new EventStatsResource.ViewStatsData(
            new EventStatsResource.ViewStatItem(totalViews, viewPercentage, "#2196F3"),
            new EventStatsResource.ViewStatItem(totalUsers - totalViews, notViewedPercentage, "#FF5722")
        );

        return new EventStatsResource(
            query.eventId().toString(),
            eventInfo.title(),
            totalUsers,
            totalViews,
            viewPercentage,
            notViewedPercentage,
            viewStats,
            departmentBreakdown
        );
    }

    // Internal methods that return domain objects
    @Override
    @Transactional(readOnly = true)
    public List<ContentView> getContentViewsByUser(GetUserAnnouncementViewsQuery query) {
        return contentViewRepository.findByUserIdAndContentTypeOrderByViewDateTimeDesc(
            query.userId(),
            ContentType.ANNOUNCEMENT
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentView> getContentViewsByUser(GetUserEventViewsQuery query) {
        return contentViewRepository.findByUserIdAndContentTypeOrderByViewDateTimeDesc(
            query.userId(),
            ContentType.EVENT
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentView> getContentViewsByContent(GetAnnouncementViewersQuery query) {
        return contentViewRepository.findByContentIdAndContentTypeOrderByViewDateTimeDesc(
            query.announcementId().value(),
            ContentType.ANNOUNCEMENT
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentView> getContentViewsByContent(GetEventViewersQuery query) {
        return contentViewRepository.findByContentIdAndContentTypeOrderByViewDateTimeDesc(
            query.eventId().value(),
            ContentType.EVENT
        );
    }

    // Private transformation methods
    private UserAnnouncementViewResource toUserAnnouncementViewResource(ContentView contentView) {
        var contentInfo = externalContentService.fetchAnnouncementInfo(new AnnouncementId(contentView.getContentId()))
            .orElse(new ExternalContentInfo(contentView.getContentId(), "Unknown Announcement", "Content not found", null, null));
        var userInfo = externalUserService.fetchUserProfile(contentView.getUserId())
            .orElse(new ExternalUserProfile(contentView.getUserId().value(), "Unknown User", "unknown@email.com", "Unknown", "Unknown"));
        
        return new UserAnnouncementViewResource(
            contentView.getId().toString(),
            contentView.getContentId().toString(),
            contentInfo.title(),
            contentInfo.description(),
            contentView.getViewDateTime(),
            contentView.getUserId().value().toString(),
            userInfo.fullName()
        );
    }

    private AnnouncementViewerResource toAnnouncementViewerResource(ContentView contentView) {
        var userInfo = externalUserService.fetchUserProfile(contentView.getUserId())
            .orElse(new ExternalUserProfile(contentView.getUserId().value(), "Unknown User", "unknown@email.com", "Unknown", "Unknown"));
        var contentInfo = externalContentService.fetchAnnouncementInfo(new AnnouncementId(contentView.getContentId()))
            .orElse(new ExternalContentInfo(contentView.getContentId(), "Unknown Announcement", "Content not found", null, null));
        
        return new AnnouncementViewerResource(
            contentView.getId().toString(),
            contentView.getUserId().value().toString(),
            userInfo.fullName(),
            userInfo.email(),
            userInfo.department(),
            contentView.getViewDateTime(),
            contentView.getContentId().toString(),
            contentInfo.title()
        );
    }

    private UserEventViewResource toUserEventViewResource(ContentView contentView) {
        var contentInfo = externalContentService.fetchEventInfo(new EventId(contentView.getContentId()))
            .orElse(new ExternalContentInfo(contentView.getContentId(), "Unknown Event", "Event not found", "Unknown Location", LocalDateTime.now()));
        var userInfo = externalUserService.fetchUserProfile(contentView.getUserId())
            .orElse(new ExternalUserProfile(contentView.getUserId().value(), "Unknown User", "unknown@email.com", "Unknown", "Unknown"));
        
        return new UserEventViewResource(
            contentView.getId().toString(),
            contentView.getContentId().toString(),
            contentInfo.title(),
            contentInfo.description(),
            contentInfo.date(),
            contentInfo.location(),
            contentView.getViewDateTime(),
            contentView.getUserId().value().toString(),
            userInfo.fullName()
        );
    }

    private EventViewerResource toEventViewerResource(ContentView contentView) {
        var userInfo = externalUserService.fetchUserProfile(contentView.getUserId())
            .orElse(new ExternalUserProfile(contentView.getUserId().value(), "Unknown User", "unknown@email.com", "Unknown", "Unknown"));
        var contentInfo = externalContentService.fetchEventInfo(new EventId(contentView.getContentId()))
            .orElse(new ExternalContentInfo(contentView.getContentId(), "Unknown Event", "Event not found", "Unknown Location", LocalDateTime.now()));
        
        return new EventViewerResource(
            contentView.getId().toString(),
            contentView.getUserId().value().toString(),
            userInfo.fullName(),
            userInfo.email(),
            userInfo.department(),
            userInfo.position(),
            contentView.getViewDateTime(),
            contentView.getContentId().toString(),
            contentInfo.title()
        );
    }
}