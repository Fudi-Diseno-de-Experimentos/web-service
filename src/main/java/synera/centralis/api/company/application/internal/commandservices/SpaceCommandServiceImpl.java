package synera.centralis.api.company.application.internal.commandservices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.company.domain.model.aggregates.Space;
import synera.centralis.api.company.domain.model.commands.CreateSpaceCommand;
import synera.centralis.api.company.domain.model.commands.DeleteSpaceCommand;
import synera.centralis.api.company.domain.model.commands.UpdateSpaceCommand;
import synera.centralis.api.company.domain.services.SpaceCommandService;
import synera.centralis.api.company.infrastructure.persistence.jpa.repositories.SpaceRepository;
import synera.centralis.api.event.interfaces.acl.EventContextFacade;
import synera.centralis.api.shared.domain.exceptions.DuplicateResourceException;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;

@Service
public class SpaceCommandServiceImpl implements SpaceCommandService {

    private final SpaceRepository spaceRepository;
    private final EventContextFacade eventContextFacade;

    public SpaceCommandServiceImpl(SpaceRepository spaceRepository, EventContextFacade eventContextFacade) {
        this.spaceRepository = spaceRepository;
        this.eventContextFacade = eventContextFacade;
    }

    @Override
    @Transactional
    public Space handle(CreateSpaceCommand command) {
        if (spaceRepository.existsByNameAndCompanyId(command.name().trim(), command.companyId())) {
            throw new DuplicateResourceException("A space named '" + command.name().trim() + "' already exists");
        }
        var space = new Space(command);
        return spaceRepository.save(space);
    }

    @Override
    @Transactional
    public Space handle(UpdateSpaceCommand command) {
        var space = spaceRepository.findByIdAndCompanyId(command.spaceId(), command.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with ID: " + command.spaceId()));

        // Only enforce the unique-name rule when the name actually changes.
        if (command.name() != null && !command.name().trim().equals(space.getName())
                && spaceRepository.existsByNameAndCompanyId(command.name().trim(), command.companyId())) {
            throw new DuplicateResourceException("A space named '" + command.name().trim() + "' already exists");
        }

        space.update(command.name(), command.description());
        return spaceRepository.save(space);
    }

    @Override
    @Transactional
    public boolean handle(DeleteSpaceCommand command) {
        if (!spaceRepository.existsByIdAndCompanyId(command.spaceId(), command.companyId())) {
            throw new ResourceNotFoundException("Space not found with ID: " + command.spaceId());
        }
        // History remains for past events; only future bookings block deletion.
        if (eventContextFacade.spaceHasFutureBookings(command.companyId(), command.spaceId())) {
            throw new DuplicateResourceException("Space cannot be deleted while it has upcoming bookings");
        }
        spaceRepository.deleteById(command.spaceId());
        return true;
    }
}
