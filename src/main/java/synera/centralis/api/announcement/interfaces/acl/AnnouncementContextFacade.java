package synera.centralis.api.announcement.interfaces.acl;

import org.springframework.stereotype.Service;
import synera.centralis.api.announcement.domain.model.queries.GetAllAnnouncementsQuery;
import synera.centralis.api.announcement.domain.model.queries.GetAnnouncementByIdQuery;
import synera.centralis.api.announcement.domain.services.AnnouncementQueryService;
import synera.centralis.api.dashboard.application.internal.outboundservices.acl.ExternalContentInfo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AnnouncementContextFacade
 * <p>
 *     This class is a facade for the Announcement context. It provides a simple interface for other bounded contexts 
 *     to interact with the announcement context.
 *     This class is a part of the ACL layer.
 * </p>
 */
@Service
public class AnnouncementContextFacade {

    private final AnnouncementQueryService announcementQueryService;

    public AnnouncementContextFacade(AnnouncementQueryService announcementQueryService) {
        this.announcementQueryService = announcementQueryService;
    }

    /**
     * Get announcement information for dashboard analytics
     * @param announcementId The announcement ID
     * @return Optional ExternalContentInfo
     */
    public Optional<ExternalContentInfo> getAnnouncementInfo(UUID announcementId) {
        try {
            var query = new GetAnnouncementByIdQuery(announcementId);
            var result = announcementQueryService.handle(query);
            
            if (result.isEmpty()) {
                return Optional.empty();
            }
            
            var announcement = result.get();
            return Optional.of(new ExternalContentInfo(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getDescription(),
                null, // No location for announcements
                convertToLocalDateTime(announcement.getCreatedAt())  // Convert Date to LocalDateTime
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Get announcement information for multiple announcements (batch operation)
     * @param announcementIds List of announcement IDs
     * @return Map of announcementId to ExternalContentInfo
     */
    public Map<UUID, ExternalContentInfo> getAnnouncementsInfo(List<UUID> announcementIds) {
        return announcementIds.stream()
            .map(this::getAnnouncementInfo)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toMap(
                ExternalContentInfo::contentId,
                info -> info
            ));
    }

    /**
     * Check if announcement exists in the system
     * @param announcementId The announcement ID
     * @return true if announcement exists, false otherwise
     */
    public boolean announcementExists(UUID announcementId) {
        try {
            var query = new GetAnnouncementByIdQuery(announcementId);
            var result = announcementQueryService.handle(query);
            return result.isPresent();
        } catch (Exception e) {
            System.err.println("Error checking if announcement exists " + announcementId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the most viewed announcement for summary statistics
     * @return Optional ExternalContentInfo of most viewed announcement
     */
    public Optional<ExternalContentInfo> getMostViewedAnnouncement() {
        try {
            // Get all announcements and return the first one as "most viewed"
            // TODO: In the future, integrate with dashboard analytics for real "most viewed" data
            var query = new GetAllAnnouncementsQuery();
            var announcements = announcementQueryService.handle(query);
            
            if (announcements.isEmpty()) {
                return Optional.empty();
            }
            
            var announcement = announcements.get(0); // Use first announcement as example
            return Optional.of(new ExternalContentInfo(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getDescription(),
                null, // No location for announcements
                convertToLocalDateTime(announcement.getCreatedAt())
            ));
        } catch (Exception e) {
            System.err.println("Error getting most viewed announcement: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get total count of announcements in the system
     * @return Total announcement count
     */
    public long getTotalAnnouncementCount() {
        try {
            // TODO: Implement actual count query
            // return announcementQueryService.getTotalCount();
            
            // For now, return mock count
            return 25L;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * Get recent announcements for dashboard display
     * @param limit Number of recent announcements to return
     * @return List of recent announcements
     */
    public List<ExternalContentInfo> getRecentAnnouncements(int limit) {
        try {
            // TODO: Implement actual recent announcements query
            // var query = new GetRecentAnnouncementsQuery(limit);
            // var result = announcementQueryService.handle(query);
            
            // For now, return mock data
            return List.of(
                new ExternalContentInfo(
                    UUID.randomUUID(),
                    "Nuevo Protocolo de Seguridad",
                    "Se ha implementado un nuevo protocolo de seguridad...",
                    null,
                    null
                ),
                new ExternalContentInfo(
                    UUID.randomUUID(),
                    "Cambios en Horarios de Trabajo",
                    "A partir del próximo mes habrá cambios en los horarios...",
                    null,
                    null
                )
            ).subList(0, Math.min(limit, 2));
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Get announcements by priority for dashboard filtering
     * @param priority Priority level (HIGH, MEDIUM, LOW)
     * @return List of announcements with the specified priority
     */
    public List<ExternalContentInfo> getAnnouncementsByPriority(String priority) {
        try {
            // TODO: Implement actual priority-based query
            // var query = new GetAnnouncementsByPriorityQuery(priority);
            // var result = announcementQueryService.handle(query);
            
            // For now, return empty list
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Helper method to convert java.util.Date to java.time.LocalDateTime
     * @param date The Date to convert
     * @return LocalDateTime or null if date is null
     */
    private LocalDateTime convertToLocalDateTime(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}