package synera.centralis.api.company.interfaces.rest.transform;

import synera.centralis.api.company.domain.model.commands.UpdateSpaceCommand;
import synera.centralis.api.company.interfaces.rest.resources.UpdateSpaceResource;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.UUID;

public class UpdateSpaceCommandFromResourceAssembler {

    public static UpdateSpaceCommand toCommandFromResource(UUID spaceId, UpdateSpaceResource resource, CompanyId companyId) {
        return new UpdateSpaceCommand(spaceId, resource.name(), resource.description(), companyId);
    }
}
