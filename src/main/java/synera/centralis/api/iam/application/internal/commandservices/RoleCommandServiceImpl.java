package synera.centralis.api.iam.application.internal.commandservices;

/*
import quri.teelab.api.teelab.iam.domain.model.commands.SeedRolesCommand;
import quri.teelab.api.teelab.iam.domain.model.entities.Role;
import quri.teelab.api.teelab.iam.domain.model.valueobjects.Roles;
import quri.teelab.api.teelab.iam.domain.services.RoleCommandService;
import quri.teelab.api.teelab.iam.infrastructure.persistence.jpa.repositories.RoleRepository;*/


import synera.centralis.api.iam.domain.model.commands.SeedRolesCommand;
import synera.centralis.api.iam.domain.model.entities.Role;
import synera.centralis.api.iam.domain.model.valueobjects.Roles;
import synera.centralis.api.iam.domain.services.RoleCommandService;
import synera.centralis.api.iam.infrastructure.persistence.jpa.repositories.RoleRepository;

import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Implementation of {@link RoleCommandService} to handle {@link SeedRolesCommand}
 */
@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * This method will handle the {@link SeedRolesCommand} and will create the roles if not exists
     * @param command {@link SeedRolesCommand}
     * @see SeedRolesCommand
     */
    @Override
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values()).forEach(role -> {
            if(!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(Roles.valueOf(role.name())));
            }
        } );
    }
}
