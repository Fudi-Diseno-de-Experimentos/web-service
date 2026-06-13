package synera.centralis.api.dashboard.interfaces.rest.sse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics SSE", description = "Server-Sent Events for real-time analytics updates")
public class AnalyticsSseController {

    private final AnalyticsSseService analyticsSseService;

    public AnalyticsSseController(AnalyticsSseService analyticsSseService) {
        this.analyticsSseService = analyticsSseService;
    }

    /**
     * Subscribe to real-time analytics updates for a specific content.
     * This endpoint requires JWT authentication.
     *
     * @param contentType the type of content (ANNOUNCEMENT or EVENT)
     * @param contentId the ID of the content
     * @return the SSE stream emitter
     */
    @GetMapping(value = "/stream/{contentType}/{contentId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to real-time analytics updates", description = "Establishes a Server-Sent Events stream to receive updates when views are registered.")
    public SseEmitter streamAnalytics(
            @PathVariable String contentType,
            @PathVariable String contentId) {
        return analyticsSseService.subscribe(contentType, contentId);
    }
}
