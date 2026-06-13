package synera.centralis.api.dashboard.application.internal.sse;

import org.springframework.context.ApplicationEvent;
import synera.centralis.api.dashboard.domain.model.aggregates.ContentView;

/**
 * Spring Application Event triggered when a new ContentView is registered.
 */
public class ContentViewRegisteredEvent extends ApplicationEvent {
    private final ContentView contentView;

    public ContentViewRegisteredEvent(Object source, ContentView contentView) {
        super(source);
        this.contentView = contentView;
    }

    public ContentView getContentView() {
        return contentView;
    }
}
