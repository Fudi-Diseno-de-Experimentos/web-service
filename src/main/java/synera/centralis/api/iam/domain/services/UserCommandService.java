package synera.centralis.api.iam.domain.services;


import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;

import synera.centralis.api.iam.domain.model.aggregates.User;
import synera.centralis.api.iam.domain.model.commands.SignInCommand;
import synera.centralis.api.iam.domain.model.commands.SignUpCommand;
import synera.centralis.api.iam.domain.model.commands.UpdateUserCommand;
import synera.centralis.api.iam.domain.model.commands.AssignUserToCompanyCommand;

/**
 * User command service
 * <p>
 *     This interface represents the service to handle user commands.
 * </p>
 */
public interface UserCommandService {
    /**
     * Handle sign in command
     * @param command the {@link SignInCommand} command
     * @return an {@link ImmutablePair} of {@link User} and {@link String}
     */
    ImmutablePair<User, String> handle(SignInCommand command);

    /**
     * Handle sign up command
     * @param command the {@link SignUpCommand} command
     * @return a {@link User} entity
     */
    User handle(SignUpCommand command);

    /**
     * Handle update user command
     * @param command the {@link UpdateUserCommand} command
     * @return a {@link User} entity
     */
    User handle(UpdateUserCommand command);

    /**
     * Handle assign user to company command
     * @param command the {@link AssignUserToCompanyCommand} command
     * @return a {@link User} entity
     */
    User handle(AssignUserToCompanyCommand command);
}
