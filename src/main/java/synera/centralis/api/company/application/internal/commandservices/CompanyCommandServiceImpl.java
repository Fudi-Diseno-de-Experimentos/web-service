package synera.centralis.api.company.application.internal.commandservices;

import org.springframework.stereotype.Service;
import synera.centralis.api.company.domain.model.aggregates.Company;
import synera.centralis.api.company.domain.model.commands.CreateCompanyCommand;
import synera.centralis.api.company.domain.model.commands.DeleteCompanyCommand;
import synera.centralis.api.company.domain.model.commands.UpdateCompanyCommand;
import synera.centralis.api.company.domain.services.CompanyCommandService;
import synera.centralis.api.company.infrastructure.persistence.jpa.repositories.CompanyRepository;

import java.util.Optional;

@Service
public class CompanyCommandServiceImpl implements CompanyCommandService {

    private final CompanyRepository companyRepository;

    public CompanyCommandServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Optional<Company> handle(CreateCompanyCommand command) {
        var company = new Company(command);
        var createdCompany = companyRepository.save(company);
        return Optional.of(createdCompany);
    }

    @Override
    public Optional<Company> handle(UpdateCompanyCommand command) {
        var result = companyRepository.findById(command.id());
        if (result.isEmpty()) throw new IllegalArgumentException("Company does not exist");
        var companyToUpdate = result.get();
        companyToUpdate.update(command.ruc(), command.nombre(), command.iconUrl(), command.isActive());
        var updatedCompany = companyRepository.save(companyToUpdate);
        return Optional.of(updatedCompany);
    }

    @Override
    public void handle(DeleteCompanyCommand command) {
        if (!companyRepository.existsById(command.id())) {
            throw new IllegalArgumentException("Company does not exist");
        }
        companyRepository.deleteById(command.id());
    }
}
