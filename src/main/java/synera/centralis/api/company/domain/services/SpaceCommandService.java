package synera.centralis.api.company.domain.services;

import synera.centralis.api.company.domain.model.aggregates.Space;
import synera.centralis.api.company.domain.model.commands.CreateSpaceCommand;
import synera.centralis.api.company.domain.model.commands.DeleteSpaceCommand;
import synera.centralis.api.company.domain.model.commands.UpdateSpaceCommand;

public interface SpaceCommandService {
    Space handle(CreateSpaceCommand command);
    Space handle(UpdateSpaceCommand command);
    boolean handle(DeleteSpaceCommand command);
}
