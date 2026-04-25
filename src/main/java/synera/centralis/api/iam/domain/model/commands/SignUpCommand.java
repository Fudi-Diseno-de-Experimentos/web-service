package synera.centralis.api.iam.domain.model.commands;

import java.util.List;

import synera.centralis.api.iam.domain.model.entities.Role;

/**
 * Sign up command
 * <p>
 *     This class represents the command to sign up a user.
 * </p>
 * @param username the username of the user
 * @param password the password of the user
 * @param firstName the first name for the profile
 * @param lastName the last name for the profile
 * @param email the email for the profile
 * @param roles the roles of the user
 */
public record SignUpCommand(
    String username, 
    String password, 
    String firstName, 
    String lastName, 
    String email, 
    List<Role> roles
) {
    public SignUpCommand {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }
}
