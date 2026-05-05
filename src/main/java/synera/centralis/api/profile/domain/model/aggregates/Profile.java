package synera.centralis.api.profile.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import synera.centralis.api.profile.domain.model.valueobjects.UserId;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.UUID;

/**
 * Profile aggregate root
 * This class represents the aggregate root for the Profile entity.
 *
 * @see AuditableAbstractAggregateRoot
 */
@Getter
@Setter
@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    @Embedded
    @NotNull
    private UserId userId;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @Email
    @NotBlank
    @Size(max = 150)
    private String email;

    @Size(max = 255)
    private String avatarUrl;

    @Embedded
    private CompanyId companyId;

    public Profile() {
    }

    public Profile(UserId userId, String firstName, String lastName, String email, String avatarUrl) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    /**
     * Create profile factory method
     * @param userId the user ID from IAM context
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email
     * @param avatarUrl the avatar URL
     * @return the created profile
     */
    public static Profile createProfile(UUID userId, String firstName, String lastName, String email, String avatarUrl) {
        return new Profile(new UserId(userId), firstName, lastName, email, avatarUrl);
    }

    /**
     * Update profile information
     * @param firstName the new first name
     * @param lastName the new last name
     * @param email the new email
     * @param avatarUrl the new avatar URL
     * @return the updated profile
     */
    public Profile updateProfile(String firstName, String lastName, String email, String avatarUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        return this;
    }

    /**
     * Get full name
     * @return the full name (firstName + lastName)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if profile belongs to user
     * @param userId the user ID to check
     * @return true if profile belongs to user
     */
    public boolean belongsToUser(UUID userId) {
        return this.userId.userId().equals(userId);
    }
}