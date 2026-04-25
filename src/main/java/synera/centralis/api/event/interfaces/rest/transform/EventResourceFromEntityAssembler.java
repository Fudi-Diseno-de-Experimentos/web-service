package synera.centralis.api.event.interfaces.rest.transform;

import synera.centralis.api.event.domain.model.agreggates.Event;
import synera.centralis.api.event.interfaces.rest.resources.EventResource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

/**
 * Assembler to convert Event entity to EventResource.
 */
public class EventResourceFromEntityAssembler {

    public static EventResource toResourceFromEntity(Event event) {
        return new EventResource(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getLocation(),
                event.getCreatedBy().userId(),
                event.getRecipients().stream()
                        .map(recipient -> recipient.userId())
                        .collect(Collectors.toList()),
                LocalDateTime.ofInstant(event.getCreatedAt().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(event.getUpdatedAt().toInstant(), ZoneId.systemDefault())
        );
    }
}


