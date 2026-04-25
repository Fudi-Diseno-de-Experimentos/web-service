package synera.centralis.api.iam.domain.model.commands;

import java.util.UUID;

/**
 * Update user command
 * Command to update user password in IAM context
 */
public record UpdateUserCommand(
    UUID userId,
    String newPassword
) {
    public UpdateUserCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (newPassword.length() > 120) {
            throw new IllegalArgumentException("Password must not exceed 120 characters");
        }
    }
}
