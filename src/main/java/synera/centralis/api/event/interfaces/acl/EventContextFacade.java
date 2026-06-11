package synera.centralis.api.event.interfaces.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import synera.centralis.api.event.domain.model.queries.GetAllEventsQuery;
import synera.centralis.api.event.domain.model.queries.GetEventByIdQuery;
import synera.centralis.api.event.domain.services.EventQueryService;
import synera.centralis.api.dashboard.application.internal.outboundservices.acl.ExternalContentInfo;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * EventContextFacade
 * <p>
 *     This class is a facade for the Event context. It provides a simple interface for other bounded contexts 
 *     to interact with the event context.
 *     This class is a part of the ACL layer.
 * </p>
 */
@Service
public class EventContextFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventContextFacade.class);

    private final EventQueryService eventQueryService;
    private final IamContextFacade iamContextFacade;

    public EventContextFacade(EventQueryService eventQueryService, IamContextFacade iamContextFacade) {
        this.eventQueryService = eventQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    private CompanyId getCurrentCompanyId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        String username = authentication.getName();
        java.util.UUID companyId = iamContextFacade.fetchCompanyIdByUsername(username);
        return companyId != null ? new CompanyId(companyId) : null;
    }

    /**
     * Get event information for dashboard analytics
     * @param eventId The event ID
     * @return Optional ExternalContentInfo
     */
    public Optional<ExternalContentInfo> getEventInfo(UUID eventId) {
        try {
            var companyId = getCurrentCompanyId();
            if (companyId == null) return Optional.empty();
            var query = new GetEventByIdQuery(eventId, companyId);
            var result = eventQueryService.handle(query);
            
            if (result.isEmpty()) {
                return Optional.empty();
            }
            
            var event = result.get();
            return Optional.of(new ExternalContentInfo(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getDate()
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Get event information for multiple events (batch operation)
     * @param eventIds List of event IDs
     * @return Map of eventId to ExternalContentInfo
     */
    public Map<UUID, ExternalContentInfo> getEventsInfo(List<UUID> eventIds) {
        return eventIds.stream()
            .map(this::getEventInfo)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toMap(
                ExternalContentInfo::contentId,
                info -> info
            ));
    }

    /**
     * Check if event exists in the system
     * @param eventId The event ID
     * @return true if event exists, false otherwise
     */
    public boolean eventExists(UUID eventId) {
        try {
            var companyId = getCurrentCompanyId();
            if (companyId == null) return false;
            var query = new GetEventByIdQuery(eventId, companyId);
            var result = eventQueryService.handle(query);
            return result.isPresent();
        } catch (Exception e) {
            LOGGER.error("Error checking if event exists {}: {}", eventId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get the most viewed event for summary statistics
     * @return Optional ExternalContentInfo of most viewed event
     */
    public Optional<ExternalContentInfo> getMostViewedEvent() {
        try {
            var companyId = getCurrentCompanyId();
            if (companyId == null) return Optional.empty();
            // Get all events and return the first one as "most viewed"
            // TODO: In the future, integrate with dashboard analytics for real "most viewed" data
            var query = new GetAllEventsQuery(companyId);
            var events = eventQueryService.handle(query);
            
            if (events.isEmpty()) {
                return Optional.empty();
            }
            
            var event = events.get(0); // Use first event as example
            return Optional.of(new ExternalContentInfo(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getDate()
            ));
        } catch (Exception e) {
            LOGGER.error("Error getting most viewed event: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get total count of events in the system
     * @return Total event count
     */
    public long getTotalEventCount() {
        try {
            // TODO: Implement actual count query
            // return eventQueryService.getTotalCount();
            
            // For now, return mock count
            return 15L;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * Get upcoming events for dashboard display
     * @param limit Number of upcoming events to return
     * @return List of upcoming events
     */
    public List<ExternalContentInfo> getUpcomingEvents(int limit) {
        try {
            // TODO: Implement actual upcoming events query
            // var query = new GetUpcomingEventsQuery(limit);
            // var result = eventQueryService.handle(query);
            
            // For now, return mock data
            return List.of(
                new ExternalContentInfo(
                    UUID.randomUUID(),
                    "Capacitación en Nuevas Tecnologías",
                    "Aprende sobre las últimas tecnologías y herramientas de desarrollo.",
                    "Sala de Conferencias A",
                    LocalDateTime.now().plusDays(7)
                ),
                new ExternalContentInfo(
                    UUID.randomUUID(),
                    "Reunión Mensual de Departamento",
                    "Revisión de objetivos mensuales y planificación de proyectos.",
                    "Sala de Reuniones B",
                    LocalDateTime.now().plusDays(3)
                )
            ).subList(0, Math.min(limit, 2));
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Get events by category for dashboard filtering
     * @param category Event category (TRAINING, MEETING, SOCIAL, etc.)
     * @return List of events with the specified category
     */
    public List<ExternalContentInfo> getEventsByCategory(String category) {
        try {
            // TODO: Implement actual category-based query
            // var query = new GetEventsByCategoryQuery(category);
            // var result = eventQueryService.handle(query);
            
            // For now, return empty list
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Get events by date range for dashboard analytics
     * @param startDate Start date for the range
     * @param endDate End date for the range
     * @return List of events within the specified date range
     */
    public List<ExternalContentInfo> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // TODO: Implement actual date range query
            // var query = new GetEventsByDateRangeQuery(startDate, endDate);
            // var result = eventQueryService.handle(query);
            
            // For now, return empty list
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Get event participation statistics for dashboard analytics
     * @param eventId The event ID
     * @return Map with participation statistics (registered, attended, etc.)
     */
    public Map<String, Long> getEventParticipationStats(UUID eventId) {
        try {
            var companyId = getCurrentCompanyId();
            if (companyId == null) return Map.of("registered", 0L, "attended", 0L, "no_show", 0L);
            // Get the actual event to retrieve real recipient data
            var query = new GetEventByIdQuery(eventId, companyId);
            var eventOptional = eventQueryService.handle(query);
            
            if (eventOptional.isPresent()) {
                var event = eventOptional.get();
                long registeredCount = event.getRecipientCount(); // Real number of recipients for this event
                
                return Map.of(
                    "registered", registeredCount,
                    "attended", 0L,  // TODO: Implement actual attendance tracking
                    "no_show", 0L    // TODO: Implement actual no-show tracking
                );
            } else {
                // Event not found, return empty stats
                return Map.of(
                    "registered", 0L,
                    "attended", 0L,
                    "no_show", 0L
                );
            }
        } catch (Exception e) {
            LOGGER.error("Error getting event participation stats for {}: {}", eventId, e.getMessage(), e);
            // Fallback to minimal stats if there's an error
            return Map.of(
                "registered", 0L,
                "attended", 0L,
                "no_show", 0L
            );
        }
    }
}