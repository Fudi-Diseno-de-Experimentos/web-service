package synera.centralis.api.dashboard.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import synera.centralis.api.dashboard.domain.model.commands.RegisterAnnouncementViewCommand;
import synera.centralis.api.dashboard.domain.model.commands.RegisterEventViewCommand;
import synera.centralis.api.dashboard.domain.model.valueobjects.*;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ContentView aggregate root representing a user's view of content (announcement or event)
 * Implements "one view per content per user forever" business rule
 */
@Entity
@Table(
    name = "content_views",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_content_views_user_content", 
            columnNames = {"user_id", "content_id", "content_type"}
        )
    }
)
@Getter
public class ContentView extends AuditableAbstractAggregateRoot<ContentView> {

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "user_id", nullable = false))
    })
    private UserId userId;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;

    @Column(name = "view_date_time", nullable = false)
    private LocalDateTime viewDateTime;

    protected ContentView() {
        super();
    }

    /**
     * Create ContentView for announcement
     * @param command RegisterAnnouncementViewCommand
     */
    public ContentView(RegisterAnnouncementViewCommand command) {
        this();
        this.userId = command.userId();
        this.contentId = command.announcementId().value();
        this.contentType = ContentType.ANNOUNCEMENT;
        this.viewDate = LocalDate.now();
        this.viewDateTime = LocalDateTime.now();
    }

    /**
     * Create ContentView for event
     * @param command RegisterEventViewCommand
     */
    public ContentView(RegisterEventViewCommand command) {
        this();
        this.userId = command.userId();
        this.contentId = command.eventId().value();
        this.contentType = ContentType.EVENT;
        this.viewDate = LocalDate.now();
        this.viewDateTime = LocalDateTime.now();
    }

    /**
     * Get ContentViewId from inherited UUID id
     * @return ContentViewId wrapping the UUID
     */
    public ContentViewId getContentViewId() {
        return new ContentViewId(getId());
    }

    /**
     * Get AnnouncementId if this view is for an announcement
     * @return AnnouncementId or throw exception if not announcement
     */
    public AnnouncementId getAnnouncementId() {
        if (contentType != ContentType.ANNOUNCEMENT) {
            throw new IllegalStateException("This content view is not for an announcement");
        }
        return new AnnouncementId(contentId);
    }

    /**
     * Get EventId if this view is for an event
     * @return EventId or throw exception if not event
     */
    public EventId getEventId() {
        if (contentType != ContentType.EVENT) {
            throw new IllegalStateException("This content view is not for an event");
        }
        return new EventId(contentId);
    }

    /**
     * Check if this view represents the same user-content combination
     * Used for duplicate detection
     */
    public boolean isSameUserAndContent(UserId userId, UUID contentId, ContentType contentType) {
        return this.userId.equals(userId) && 
               this.contentId.equals(contentId) && 
               this.contentType == contentType;
    }
}