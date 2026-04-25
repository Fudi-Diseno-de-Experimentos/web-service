package synera.centralis.api.dashboard.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;
import synera.centralis.api.announcement.interfaces.acl.AnnouncementContextFacade;
import synera.centralis.api.event.interfaces.acl.EventContextFacade;
import synera.centralis.api.dashboard.domain.model.valueobjects.AnnouncementId;
import synera.centralis.api.dashboard.domain.model.valueobjects.EventId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * External Content Service - ACL for accessing Announcement and Event data
 * Integrates with announcement and event contexts through facades with fallback to mock data
 */
@Service
public class ExternalContentService {

    private final AnnouncementContextFacade announcementContextFacade;
    private final EventContextFacade eventContextFacade;

    public ExternalContentService(AnnouncementContextFacade announcementContextFacade,
                                EventContextFacade eventContextFacade) {
        this.announcementContextFacade = announcementContextFacade;
        this.eventContextFacade = eventContextFacade;
    }

    /**
     * Fetch announcement information
     * @param announcementId The announcement ID
     * @return Optional ExternalContentInfo
     */
    public Optional<ExternalContentInfo> fetchAnnouncementInfo(AnnouncementId announcementId) {
        try {
            return announcementContextFacade.getAnnouncementInfo(announcementId.value());
        } catch (Exception e) {
            // Fallback to mock data if announcement context is not available
            return getMockAnnouncementInfo(announcementId);
        }
    }

    /**
     * Fetch event information
     * @param eventId The event ID
     * @return Optional ExternalContentInfo
     */
    public Optional<ExternalContentInfo> fetchEventInfo(EventId eventId) {
        try {
            return eventContextFacade.getEventInfo(eventId.value());
        } catch (Exception e) {
            // Fallback to mock data if event context is not available
            return getMockEventInfo(eventId);
        }
    }

    /**
     * Fetch multiple announcements (batch operation)
     * @param announcementIds List of announcement IDs
     * @return Map of announcementId to ExternalContentInfo
     */
    public Map<UUID, ExternalContentInfo> fetchAnnouncementsInfo(List<AnnouncementId> announcementIds) {
        try {
            return announcementContextFacade.getAnnouncementsInfo(
                announcementIds.stream().map(AnnouncementId::value).toList()
            );
        } catch (Exception e) {
            // Fallback to individual calls if batch operation fails
            return announcementIds.stream()
                .map(this::fetchAnnouncementInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                    ExternalContentInfo::contentId,
                    info -> info
                ));
        }
    }

    /**
     * Fetch multiple events (batch operation)
     * @param eventIds List of event IDs
     * @return Map of eventId to ExternalContentInfo
     */
    public Map<UUID, ExternalContentInfo> fetchEventsInfo(List<EventId> eventIds) {
        try {
            return eventContextFacade.getEventsInfo(
                eventIds.stream().map(EventId::value).toList()
            );
        } catch (Exception e) {
            // Fallback to individual calls if batch operation fails
            return eventIds.stream()
                .map(this::fetchEventInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                    ExternalContentInfo::contentId,
                    info -> info
                ));
        }
    }

    /**
     * Check if announcement exists
     * @param announcementId The announcement ID
     * @return true if announcement exists, false otherwise
     */
    public boolean announcementExists(AnnouncementId announcementId) {
        try {
            return announcementContextFacade.announcementExists(announcementId.value());
        } catch (Exception e) {
            // Assume exists if we can't verify
            return true;
        }
    }

    /**
     * Check if event exists
     * @param eventId The event ID
     * @return true if event exists, false otherwise
     */
    public boolean eventExists(EventId eventId) {
        try {
            return eventContextFacade.eventExists(eventId.value());
        } catch (Exception e) {
            // Assume exists if we can't verify
            return true;
        }
    }

    /**
     * Get most viewed announcement info for summary statistics
     * @return Optional ExternalContentInfo of most viewed announcement
     */
    public Optional<ExternalContentInfo> getMostViewedAnnouncementInfo() {
        try {
            return announcementContextFacade.getMostViewedAnnouncement();
        } catch (Exception e) {
            // Return mock data
            return Optional.of(new ExternalContentInfo(
                UUID.randomUUID(),
                "Nuevo Protocolo de Seguridad",
                "Se ha implementado un nuevo protocolo de seguridad...",
                null,
                null
            ));
        }
    }

    /**
     * Get most viewed event info for summary statistics
     * @return Optional ExternalContentInfo of most viewed event
     */
    public Optional<ExternalContentInfo> getMostViewedEventInfo() {
        try {
            return eventContextFacade.getMostViewedEvent();
        } catch (Exception e) {
            // Return mock data
            return Optional.of(new ExternalContentInfo(
                UUID.randomUUID(),
                "Capacitación en Nuevas Tecnologías",
                "Aprende sobre las últimas tecnologías...",
                "Sala de Conferencias A",
                LocalDateTime.now().plusDays(7)
            ));
        }
    }

    /**
     * Get event participation statistics for dashboard analytics
     * @param eventId The event ID
     * @return Map with participation statistics (registered, attended, etc.)
     */
    public Map<String, Long> fetchEventParticipationStats(EventId eventId) {
        try {
            return eventContextFacade.getEventParticipationStats(eventId.value());
        } catch (Exception e) {
            // Fallback to minimal participation stats if event context is not available
            return Map.of(
                "registered", 0L,  // Default to 0 if we can't get real data
                "attended", 0L,
                "no_show", 0L
            );
        }
    }

    // Private helper methods for fallback data
    private Optional<ExternalContentInfo> getMockAnnouncementInfo(AnnouncementId announcementId) {
        return Optional.of(new ExternalContentInfo(
            announcementId.value(),
            "Mock Announcement " + announcementId.value().toString().substring(0, 8),
            "This is a mock announcement description. Content will be loaded from announcement context when available.",
            null, // No location for announcements
            null  // No date for announcements
        ));
    }

    private Optional<ExternalContentInfo> getMockEventInfo(EventId eventId) {
        return Optional.of(new ExternalContentInfo(
            eventId.value(),
            "Mock Event " + eventId.value().toString().substring(0, 8),
            "This is a mock event description. Content will be loaded from event context when available.",
            "Sala de Conferencias A",
            LocalDateTime.now().plusDays(7)
        ));
    }
}