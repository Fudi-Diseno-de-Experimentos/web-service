package synera.centralis.api.event.interfaces.rest.transform;

import synera.centralis.api.event.domain.model.commands.CreateEventCommand;
import synera.centralis.api.event.domain.model.valueobjects.UserId;
import synera.centralis.api.event.interfaces.rest.resources.CreateEventResource;

/**
 * Assembler to convert CreateEventResource to CreateEventCommand.
 */
public class CreateEventCommandFromResourceAssembler {

    public static CreateEventCommand toCommandFromResource(CreateEventResource resource) {
        return new CreateEventCommand(
                resource.title(),
                resource.description(),
                resource.date(),
                resource.location(),
                resource.recipientIds(),
                new UserId(resource.createdBy())
        );
    }
}