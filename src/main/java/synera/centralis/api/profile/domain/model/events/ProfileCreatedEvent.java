package synera.centralis.api.profile.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Profile created event
 * Domain event fired when a new profile is created
 */
public class ProfileCreatedEvent {
    private final UUID profileId;
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final Instant occurredOn;

    public ProfileCreatedEvent(UUID profileId, Long userId, String firstName, String lastName) {
        this.profileId = profileId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.occurredOn = Instant.now();
    }

    public UUID getProfileId() {
        return profileId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}