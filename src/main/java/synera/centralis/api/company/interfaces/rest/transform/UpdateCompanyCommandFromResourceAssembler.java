package synera.centralis.api.company.interfaces.rest.transform;

import synera.centralis.api.company.domain.model.commands.UpdateCompanyCommand;
import synera.centralis.api.company.interfaces.rest.resources.UpdateCompanyResource;

import java.util.UUID;

public class UpdateCompanyCommandFromResourceAssembler {
    public static UpdateCompanyCommand toCommandFromResource(UUID id, UpdateCompanyResource resource) {
        return new UpdateCompanyCommand(id, resource.ruc(), resource.nombre(), resource.iconUrl(), resource.isActive());
    }
}
