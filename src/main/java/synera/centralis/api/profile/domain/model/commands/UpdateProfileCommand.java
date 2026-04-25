package synera.centralis.api.profile.domain.model.commands;

import java.util.UUID;

import synera.centralis.api.profile.domain.model.valueobjects.Department;
import synera.centralis.api.profile.domain.model.valueobjects.Position;

/**
 * Update profile command
 * Command to update an existing user profile
 */
public record UpdateProfileCommand(
    UUID profileId,
    String firstName,
    String lastName,
    String email,
    String avatarUrl,
    Position position,
    Department department
) {
    public UpdateProfileCommand {
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
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
        if (firstName.length() > 100) {
            throw new IllegalArgumentException("First name must not exceed 100 characters");
        }
        if (lastName.length() > 100) {
            throw new IllegalArgumentException("Last name must not exceed 100 characters");
        }
        if (email.length() > 150) {
            throw new IllegalArgumentException("Email must not exceed 150 characters");
        }
        if (avatarUrl != null && avatarUrl.length() > 255) {
            throw new IllegalArgumentException("Avatar URL must not exceed 255 characters");
        }
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
    }
}