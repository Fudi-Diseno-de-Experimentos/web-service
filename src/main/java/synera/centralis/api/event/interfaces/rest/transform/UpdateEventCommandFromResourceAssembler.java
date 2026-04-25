package synera.centralis.api.event.interfaces.rest.transform;

import synera.centralis.api.event.domain.model.commands.UpdateEventCommand;
import synera.centralis.api.event.interfaces.rest.resources.UpdateEventResource;

import java.util.UUID;

/**
 * Assembler to convert UpdateEventResource to UpdateEventCommand.
 */
public class UpdateEventCommandFromResourceAssembler {

    public static UpdateEventCommand toCommandFromResource(UUID eventId, UpdateEventResource resource) {
        return new UpdateEventCommand(
                eventId,
                resource.title(),
                resource.description(),
                resource.date(),
                resource.location(),
                resource.recipientIds()
        );
    }
}

