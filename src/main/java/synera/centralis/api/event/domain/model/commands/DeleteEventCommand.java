package synera.centralis.api.event.domain.model.commands;

import java.util.UUID;

import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Command to delete an event.
 */
public record DeleteEventCommand(UUID eventId, CompanyId companyId) {
    public DeleteEventCommand {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}


