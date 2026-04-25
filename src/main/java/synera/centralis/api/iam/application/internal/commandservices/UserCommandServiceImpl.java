package synera.centralis.api.iam.application.internal.commandservices;

import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import synera.centralis.api.iam.application.internal.outboundservices.hashing.HashingService;
import synera.centralis.api.iam.application.internal.outboundservices.tokens.TokenService;
import synera.centralis.api.iam.application.internal.outboundservices.acl.ExternalProfileService;
import synera.centralis.api.iam.domain.model.aggregates.User;
import synera.centralis.api.iam.domain.model.commands.SignInCommand;
import synera.centralis.api.iam.domain.model.commands.SignUpCommand;
import synera.centralis.api.iam.domain.model.commands.UpdateUserCommand;
import synera.centralis.api.iam.domain.services.UserCommandService;
import synera.centralis.api.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import synera.centralis.api.iam.infrastructure.persistence.jpa.repositories.UserRepository;

/**
 * User command service implementation
 * <p>
 *     This class implements the {@link UserCommandService} interface and provides the implementation for the
 *     {@link SignInCommand} and {@link SignUpCommand} commands.
 * </p>
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final ExternalProfileService externalProfileService;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService, RoleRepository roleRepository, ExternalProfileService externalProfileService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.externalProfileService = externalProfileService;
    }

    /**
     * Handle the sign-in command
     * <p>
     *     This method handles the {@link SignInCommand} command and returns the user and the token.
     * </p>
     * @param command the sign-in command containing the username and password
     * @return and optional containing the user matching the username and the generated token
     * @throws RuntimeException if the user is not found or the password is invalid
     */
    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByUsername(command.username());
        if (user.isEmpty())
            throw new RuntimeException("User not found");
        if (!hashingService.matches(command.password(), user.get().getPassword()))
            throw new RuntimeException("Invalid password");
        var token = tokenService.generateToken(user.get().getUsername());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    /**
     * Handle the sign-up command
     * <p>
     *     This method handles the {@link SignUpCommand} command and returns the user.
     * </p>
     * @param command the sign-up command containing the username and password
     * @return the created user
     */
    @Override
    public Optional<User> handle(SignUpCommand command) {
        // Validate username doesn't already exist
        if (userRepository.existsByUsername(command.username()))
            throw new RuntimeException("Username already exists");
        
        var roles = command.roles().stream()
            .map(role -> roleRepository.findByName(role.getName())
                .orElseThrow(() -> new RuntimeException("Role name not found")))
            .toList();
            
        var user = new User(
            command.username(), 
            hashingService.encode(command.password()),
            roles
        );
        
        var savedUser = userRepository.save(user);

        // Create profile automatically using ACL
        try {
            var profileCreated = externalProfileService.createBasicProfile(
                savedUser.getId().toString(), // Convert UUID to String
                command.firstName(),
                command.lastName(),
                command.email()
            );

            if (profileCreated.isEmpty()) {
                // Log warning but don't fail user creation
                System.err.println("Warning: Failed to create profile for user " + savedUser.getId());
            }
        } catch (Exception e) {
            // Log error but don't fail user creation
            System.err.println("Error creating profile for user " + savedUser.getId() + ": " + e.getMessage());
        }
        
        return userRepository.findByUsername(command.username());
    }

    /**
     * Handle the update user command
     * <p>
     *     This method handles the {@link UpdateUserCommand} command for password updates only.
     *     Other user data is managed by the Profile context.
     * </p>
     * @param command the update user command containing the user ID and new password
     * @return the updated user
     */
    @Override
    public Optional<User> handle(UpdateUserCommand command) {
        var user = userRepository.findById(command.userId());
        if (user.isEmpty())
            throw new RuntimeException("User not found");
        
        var existingUser = user.get();
        var hashedPassword = hashingService.encode(command.newPassword());
        existingUser.setPassword(hashedPassword);
        
        var updatedUser = userRepository.save(existingUser);
        return Optional.of(updatedUser);
    }
}
