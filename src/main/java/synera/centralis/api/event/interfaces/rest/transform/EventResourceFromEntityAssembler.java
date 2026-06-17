package synera.centralis.api.event.interfaces.rest.transform;

import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.interfaces.rest.resources.EventResource;
import synera.centralis.api.event.interfaces.rest.resources.RecipientResource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Assembler to convert Event entity to EventResource.
 */
public class EventResourceFromEntityAssembler {

    /**
     * Builds the resource without a known viewer; {@code myStatus} will be null.
     */
    public static EventResource toResourceFromEntity(Event event) {
        return toResourceFromEntity(event, null);
    }

    /**
     * Builds the resource and resolves {@code myStatus} for the given viewer (the authenticated
     * caller). {@code viewerUserId} may be null (e.g. non-UUID principal), yielding a null myStatus.
     */
    public static EventResource toResourceFromEntity(Event event, UUID viewerUserId) {
        var recipients = event.getRecipients().stream()
                .map(recipient -> new RecipientResource(recipient.getUserId(), recipient.effectiveStatus()))
                .collect(Collectors.toList());

        var myStatus = viewerUserId != null ? event.getStatusFor(viewerUserId) : null;

        return new EventResource(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getSpaceId() != null ? event.getSpaceId().spaceId() : null,
                event.getCreatedBy().userId(),
                recipients,
                myStatus,
                LocalDateTime.ofInstant(event.getCreatedAt().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(event.getUpdatedAt().toInstant(), ZoneId.systemDefault())
        );
    }
}
