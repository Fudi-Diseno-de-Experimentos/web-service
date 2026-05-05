package synera.centralis.api.company.domain.services;

import synera.centralis.api.company.domain.model.aggregates.Company;
import synera.centralis.api.company.domain.model.commands.CreateCompanyCommand;
import synera.centralis.api.company.domain.model.commands.DeleteCompanyCommand;
import synera.centralis.api.company.domain.model.commands.UpdateCompanyCommand;

import java.util.Optional;

public interface CompanyCommandService {
    Company handle(CreateCompanyCommand command);
    Company handle(UpdateCompanyCommand command);
    boolean handle(DeleteCompanyCommand command);
}
