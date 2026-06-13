package synera.centralis.api.dashboard.interfaces.rest.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import synera.centralis.api.dashboard.application.internal.sse.ContentViewRegisteredEvent;
import synera.centralis.api.dashboard.domain.model.aggregates.ContentView;
import synera.centralis.api.dashboard.domain.model.queries.*;
import synera.centralis.api.dashboard.domain.model.valueobjects.AnnouncementId;
import synera.centralis.api.dashboard.domain.model.valueobjects.ContentType;
import synera.centralis.api.dashboard.domain.model.valueobjects.EventId;
import synera.centralis.api.dashboard.domain.services.ContentViewQueryService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AnalyticsSseService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsSseService.class);
    
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final ContentViewQueryService contentViewQueryService;

    public AnalyticsSseService(ContentViewQueryService contentViewQueryService) {
        this.contentViewQueryService = contentViewQueryService;
    }

    /**
     * Subscribe a client to real-time analytics updates for a specific content ID.
     *
     * @param contentTypeStr the type of content (ANNOUNCEMENT or EVENT)
     * @param contentId the ID of the announcement or event
     * @return the SseEmitter instance
     */
    public SseEmitter subscribe(String contentTypeStr, String contentId) {
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L); // 24-hour timeout for active sessions
        
        List<SseEmitter> list = emitters.computeIfAbsent(contentId, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        emitter.onCompletion(() -> removeEmitter(contentId, emitter));
        emitter.onTimeout(() -> removeEmitter(contentId, emitter));
        emitter.onError((e) -> removeEmitter(contentId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected to analytics stream for " + contentId));
        } catch (IOException e) {
            removeEmitter(contentId, emitter);
            return emitter;
        }

        // Send initial payload immediately on subscription to ensure the client gets the latest data
        try {
            ContentType contentType = ContentType.fromString(contentTypeStr);
            Object initialPayload = buildPayload(contentId, contentType);
            emitter.send(SseEmitter.event()
                    .name("analytics-update")
                    .data(initialPayload, MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            log.error("Failed to send initial analytics payload for content: " + contentId, e);
        }

        return emitter;
    }

    private void removeEmitter(String contentId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(contentId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                emitters.remove(contentId);
            }
        }
    }

    /**
     * Listen to internal application events when a new view is registered and
     * broadcast the fresh statistics and viewers to subscribed emitters.
     */
    @EventListener
    @Async
    public void onContentViewRegistered(ContentViewRegisteredEvent event) {
        ContentView view = event.getContentView();
        String contentId = view.getContentId().toString();
        
        List<SseEmitter> list = emitters.get(contentId);
        if (list == null || list.isEmpty()) {
            return;
        }

        try {
            Object updatePayload = buildPayload(contentId, view.getContentType());
            broadcastUpdate(contentId, updatePayload);
        } catch (Exception e) {
            log.error("Failed to build or broadcast analytics update for content ID " + contentId, e);
        }
    }

    private Object buildPayload(String contentIdStr, ContentType contentType) {
        UUID contentId = UUID.fromString(contentIdStr);
        if (contentType == ContentType.ANNOUNCEMENT) {
            var announcementId = new AnnouncementId(contentId);
            var stats = contentViewQueryService.handle(new GetAnnouncementStatsQuery(announcementId));
            var viewers = contentViewQueryService.handle(new GetAnnouncementViewersQuery(announcementId));
            return new AnalyticsUpdatePayload(stats, viewers);
        } else {
            var eventId = new EventId(contentId);
            var stats = contentViewQueryService.handle(new GetEventStatsQuery(eventId));
            var viewers = contentViewQueryService.handle(new GetEventViewersQuery(eventId));
            return new AnalyticsUpdatePayload(stats, viewers);
        }
    }

    private void broadcastUpdate(String contentId, Object payload) {
        List<SseEmitter> list = emitters.get(contentId);
        if (list == null || list.isEmpty()) {
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event()
                        .name("analytics-update")
                        .data(payload, MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }
        
        if (!deadEmitters.isEmpty()) {
            list.removeAll(deadEmitters);
            if (list.isEmpty()) {
                emitters.remove(contentId);
            }
        }
    }

    public record AnalyticsUpdatePayload(Object stats, List<?> viewers) {}
}
