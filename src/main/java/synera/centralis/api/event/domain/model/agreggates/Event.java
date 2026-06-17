package synera.centralis.api.event.domain.model.agreggates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import synera.centralis.api.event.domain.model.valueobjects.EventRecipient;
import synera.centralis.api.event.domain.model.valueobjects.RecipientStatus;
import synera.centralis.api.event.domain.model.valueobjects.UserId;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import synera.centralis.api.event.domain.model.valueobjects.SpaceId;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Event aggregate root representing a business event.
 * Contains event information and recipient management capabilities.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "events")
public class Event extends AuditableAbstractAggregateRoot<Event> {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "created_by"))
    private UserId createdBy;

    // Eager + subselect: assemblers read this collection after the transaction
    // closes (open-in-view is disabled), and subselect avoids N+1 on list queries.
    // Each recipient carries their own invitation response status (see EventRecipient).
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "event_recipients", joinColumns = @JoinColumn(name = "event_id"))
    private Set<EventRecipient> recipients = new HashSet<>();

    @Embedded
    private CompanyId companyId;
    public void setCompanyId(CompanyId companyId) { this.companyId = companyId; }

    // Mandatory link to the company Space (room) this event books. An event
    // always occupies exactly one managed room.
    @Embedded
    private SpaceId spaceId;
    public void setSpaceId(SpaceId spaceId) { this.spaceId = validateSpaceId(spaceId); }

    /**
     * Constructor for creating a new event.
     */
    public Event(String title, String description, LocalDateTime date, SpaceId spaceId,
                 List<UUID> recipientIds, UserId createdBy) {
        this.title = validateAndSetTitle(title);
        this.description = validateAndSetDescription(description);
        this.date = validateDate(date);
        this.spaceId = validateSpaceId(spaceId);
        this.createdBy = validateCreatedBy(createdBy);
        this.recipients = new HashSet<>();

        // Add recipients, each starting as PENDING (nobody has responded yet).
        if (recipientIds != null && !recipientIds.isEmpty()) {
            recipientIds.forEach(recipientId -> this.recipients.add(new EventRecipient(recipientId)));
        }

        validateAtLeastOneRecipient();
    }

    /**
     * Updates event information.
     */
    public void updateEvent(String title, String description, LocalDateTime date,
                           SpaceId spaceId, List<UUID> recipientIds) {
        if (title != null) {
            this.title = validateAndSetTitle(title);
        }
        if (description != null) {
            this.description = validateAndSetDescription(description);
        }
        if (date != null) {
            this.date = validateDate(date);
        }
        if (spaceId != null) {
            this.spaceId = validateSpaceId(spaceId);
        }
        if (recipientIds != null) {
            updateRecipients(recipientIds);
            validateAtLeastOneRecipient();
        }
    }

    /**
     * Reconciles the recipient set against {@code recipientIds} by diffing, so existing
     * accept/decline responses survive an edit:
     * <ul>
     *     <li>recipient still present → keep their current status;</li>
     *     <li>newly added → {@link RecipientStatus#PENDING};</li>
     *     <li>removed → dropped (a later re-add comes back as PENDING).</li>
     * </ul>
     */
    private void updateRecipients(List<UUID> recipientIds) {
        Set<UUID> target = new HashSet<>(recipientIds);
        // Drop recipients no longer in the target list.
        this.recipients.removeIf(recipient -> !target.contains(recipient.getUserId()));
        // Add new recipients as PENDING; existing ones are left untouched (Set dedup by userId).
        target.forEach(userId -> this.recipients.add(new EventRecipient(userId)));
    }

    /**
     * Records a member's response to their invitation. The user must already be a recipient.
     * @throws IllegalStateException if the user is not a recipient of this event
     */
    public void respondToInvitation(UUID userId, RecipientStatus status) {
        var recipient = this.recipients.stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User is not a recipient of this event"));
        recipient.setStatus(status);
    }

    /**
     * Adds a recipient to the event (defaults to PENDING). No-op if already present.
     */
    public void addRecipient(UserId recipientId) {
        if (recipientId == null) {
            throw new IllegalArgumentException("Recipient ID cannot be null");
        }
        this.recipients.add(new EventRecipient(recipientId.userId()));
    }

    /**
     * Removes a recipient from the event.
     */
    public void removeRecipient(UserId recipientId) {
        if (recipientId == null) {
            throw new IllegalArgumentException("Recipient ID cannot be null");
        }
        this.recipients.removeIf(r -> r.getUserId().equals(recipientId.userId()));
    }

    /**
     * Checks if a user is a recipient of this event.
     */
    public boolean isRecipient(UserId userId) {
        if (userId == null) {
            return false;
        }
        return this.recipients.stream().anyMatch(r -> r.getUserId().equals(userId.userId()));
    }

    /**
     * The invitation status of the given user, or {@code null} if they are not a recipient.
     */
    public RecipientStatus getStatusFor(UUID userId) {
        return this.recipients.stream()
                .filter(r -> r.getUserId().equals(userId))
                .map(EventRecipient::effectiveStatus)
                .findFirst()
                .orElse(null);
    }

    /**
     * The user IDs of all recipients, regardless of status (e.g. for notifications).
     */
    public Set<UUID> getRecipientUserIds() {
        return this.recipients.stream()
                .map(EventRecipient::getUserId)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the number of recipients.
     */
    public int getRecipientCount() {
        return this.recipients.size();
    }

    // Validation methods
    private String validateAndSetTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be null or empty");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Event title cannot exceed 200 characters");
        }
        return title.trim();
    }

    private String validateAndSetDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Event description cannot be null or empty");
        }
        if (description.length() > 1000) {
            throw new IllegalArgumentException("Event description cannot exceed 1000 characters");
        }
        return description.trim();
    }

    private LocalDateTime validateDate(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }
        return date;
    }

    private SpaceId validateSpaceId(SpaceId spaceId) {
        if (spaceId == null) {
            throw new IllegalArgumentException("Event space cannot be null");
        }
        return spaceId;
    }

    private UserId validateCreatedBy(UserId createdBy) {
        if (createdBy == null) {
            throw new IllegalArgumentException("Created by cannot be null");
        }
        return createdBy;
    }

    private void validateAtLeastOneRecipient() {
        if (this.recipients.isEmpty()) {
            throw new IllegalArgumentException("Event must have at least one recipient");
        }
    }
}

