package synera.centralis.api.iam.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import synera.centralis.api.iam.domain.model.commands.AssignUserToCompanyCommand;
import synera.centralis.api.iam.domain.services.UserCommandService;
import synera.centralis.api.shared.domain.events.CompanyCreatedEvent;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Event handler for CompanyCreatedEvent.
 * Automatically assigns the creator user to the newly created company.
 */
@Service
public class CompanyCreatedEventHandler {
    private final UserCommandService userCommandService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyCreatedEventHandler.class);

    public CompanyCreatedEventHandler(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @EventListener
    public void on(CompanyCreatedEvent event) {
        LOGGER.info("Handling CompanyCreatedEvent for company {} and user {}", event.companyId(), event.userId());
        if (event.userId() == null) {
            LOGGER.warn("User ID in CompanyCreatedEvent is null. Skipping assignment.");
            return;
        }
        try {
            var command = new AssignUserToCompanyCommand(
                event.userId(),
                new CompanyId(event.companyId())
            );
            userCommandService.handle(command);
            LOGGER.info("Successfully assigned user {} to company {}", event.userId(), event.companyId());
        } catch (Exception e) {
            LOGGER.error("Error assigning user {} to company {}: {}", event.userId(), event.companyId(), e.getMessage());
        }
    }
}
