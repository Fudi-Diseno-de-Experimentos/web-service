package synera.centralis.api.dashboard.domain.services;

import synera.centralis.api.dashboard.domain.model.aggregates.ContentView;
import synera.centralis.api.dashboard.domain.model.commands.RegisterAnnouncementViewCommand;
import synera.centralis.api.dashboard.domain.model.commands.RegisterEventViewCommand;

import java.util.Optional;

/**
 * Command service interface for content view operations
 */
public interface ContentViewCommandService {

    /**
     * Handle announcement view registration
     * Implements "one view per content per user forever" rule
     * @param command RegisterAnnouncementViewCommand
     * @return ContentView (new or existing)
     */
    Optional<ContentView> handle(RegisterAnnouncementViewCommand command);

    /**
     * Handle event view registration  
     * Implements "one view per content per user forever" rule
     * @param command RegisterEventViewCommand
     * @return ContentView (new or existing)
     */
    Optional<ContentView> handle(RegisterEventViewCommand command);
}