package synera.centralis.api.dashboard.application.internal.commandservices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.dashboard.domain.model.aggregates.ContentView;
import synera.centralis.api.dashboard.domain.model.commands.RegisterAnnouncementViewCommand;
import synera.centralis.api.dashboard.domain.model.commands.RegisterEventViewCommand;
import synera.centralis.api.dashboard.domain.model.valueobjects.ContentType;
import synera.centralis.api.dashboard.domain.services.ContentViewCommandService;
import synera.centralis.api.dashboard.infrastructure.persistence.jpa.repositories.ContentViewRepository;

import java.util.Optional;

/**
 * Implementation of ContentViewCommandService
 * Handles command operations for content views with anti-duplication logic
 */
@Service
public class ContentViewCommandServiceImpl implements ContentViewCommandService {

    private final ContentViewRepository contentViewRepository;

    public ContentViewCommandServiceImpl(ContentViewRepository contentViewRepository) {
        this.contentViewRepository = contentViewRepository;
    }

    /**
     * Register announcement view with duplicate prevention
     * Business Rule: One view per content per user forever
     */
    @Override
    @Transactional
    public Optional<ContentView> handle(RegisterAnnouncementViewCommand command) {
        // Check if user already viewed this announcement
        var existingView = contentViewRepository.findByUserIdAndContentIdAndContentType(
            command.userId(),
            command.announcementId().value(),
            ContentType.ANNOUNCEMENT
        );

        if (existingView.isPresent()) {
            // Return existing view - no duplicate created
            return existingView;
        }

        // Create new view - first time viewing this content
        var newView = new ContentView(command);
        var savedView = contentViewRepository.save(newView);
        return Optional.of(savedView);
    }

    /**
     * Register event view with duplicate prevention  
     * Business Rule: One view per content per user forever
     */
    @Override
    @Transactional
    public Optional<ContentView> handle(RegisterEventViewCommand command) {
        // Check if user already viewed this event
        var existingView = contentViewRepository.findByUserIdAndContentIdAndContentType(
            command.userId(),
            command.eventId().value(),
            ContentType.EVENT
        );

        if (existingView.isPresent()) {
            // Return existing view - no duplicate created
            return existingView;
        }

        // Create new view - first time viewing this content
        var newView = new ContentView(command);
        var savedView = contentViewRepository.save(newView);
        return Optional.of(savedView);
    }
}