package synera.centralis.api.event.domain.model.commands;

import synera.centralis.api.event.domain.model.valueobjects.RecipientStatus;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.UUID;

/**
 * Command for a member to respond to their event invitation (accept or decline).
 * The {@code userId} is always the authenticated caller — members respond only for themselves.
 */
public record RespondToEventInvitationCommand(UUID eventId, UUID userId, RecipientStatus status, CompanyId companyId) {
    public RespondToEventInvitationCommand {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (status != RecipientStatus.ACCEPTED && status != RecipientStatus.DECLINED) {
            throw new IllegalArgumentException("Response status must be ACCEPTED or DECLINED");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
    }
}
