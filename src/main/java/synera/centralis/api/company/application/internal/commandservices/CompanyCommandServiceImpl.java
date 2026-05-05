package synera.centralis.api.company.application.internal.commandservices;

import org.springframework.stereotype.Service;
import synera.centralis.api.company.domain.model.aggregates.Company;
import synera.centralis.api.company.domain.model.commands.CreateCompanyCommand;
import synera.centralis.api.company.domain.model.commands.DeleteCompanyCommand;
import synera.centralis.api.company.domain.model.commands.UpdateCompanyCommand;
import synera.centralis.api.company.domain.services.CompanyCommandService;
import synera.centralis.api.company.infrastructure.persistence.jpa.repositories.CompanyRepository;

import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;
import synera.centralis.api.shared.domain.exceptions.ValidationException;

@Service
public class CompanyCommandServiceImpl implements CompanyCommandService {

    private final CompanyRepository companyRepository;

    public CompanyCommandServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company handle(CreateCompanyCommand command) {
        var company = new Company(command);
        return companyRepository.save(company);
    }

    @Override
    public Company handle(UpdateCompanyCommand command) {
        var companyToUpdate = companyRepository.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Company does not exist"));
        
        companyToUpdate.update(command.ruc(), command.nombre(), command.iconUrl(), command.isActive());
        return companyRepository.save(companyToUpdate);
    }

    @Override
    public boolean handle(DeleteCompanyCommand command) {
        if (!companyRepository.existsById(command.id())) {
            throw new ResourceNotFoundException("Company does not exist");
        }
        companyRepository.deleteById(command.id());
        return true;
    }
}
