package synera.centralis.api.company.application.internal.commandservices;

import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import synera.centralis.api.company.domain.model.aggregates.Company;
import synera.centralis.api.company.domain.model.commands.CreateCompanyCommand;
import synera.centralis.api.company.domain.model.commands.DeleteCompanyCommand;
import synera.centralis.api.company.domain.model.commands.UpdateCompanyCommand;
import synera.centralis.api.company.domain.services.CompanyCommandService;
import synera.centralis.api.company.infrastructure.persistence.jpa.repositories.CompanyRepository;

import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;
import synera.centralis.api.shared.domain.exceptions.ValidationException;
import synera.centralis.api.shared.domain.events.CompanyCreatedEvent;

@Service
public class CompanyCommandServiceImpl implements CompanyCommandService {

    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CompanyCommandServiceImpl(CompanyRepository companyRepository, ApplicationEventPublisher eventPublisher) {
        this.companyRepository = companyRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Company handle(CreateCompanyCommand command) {
        if (command.userId() != null) {
            var userId = new synera.centralis.api.company.domain.model.valueobjects.UserId(command.userId());
            if (companyRepository.findFirstByUserId(userId).isPresent()) {
                throw new ValidationException("User already has a company assigned or created");
            }
        }
        var company = new Company(command);
        // The join code is unique-constrained; on the rare collision, regenerate
        // before saving so we never surface a raw DataIntegrityViolation as a 500.
        int attempts = 0;
        while (companyRepository.findByJoinCode(company.getJoinCode()).isPresent()) {
            if (++attempts > 10) {
                throw new ValidationException("Could not generate a unique join code, please retry");
            }
            company.regenerateJoinCode();
        }
        var savedCompany = companyRepository.save(company);
        if (command.userId() != null) {
            eventPublisher.publishEvent(CompanyCreatedEvent.create(savedCompany.getId(), command.userId()));
        }
        return savedCompany;
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
