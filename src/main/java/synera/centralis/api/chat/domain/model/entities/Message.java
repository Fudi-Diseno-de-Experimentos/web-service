package synera.centralis.api.chat.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synera.centralis.api.chat.domain.model.valueobjects.MessageStatus;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.Date;
import java.util.UUID;

/**
 * Message entity representing a chat message within a group.
 * Contains message content and metadata.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "sender_id"))
    private UserId senderId;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_at", nullable = false)
    private Date sentAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "edited_at")
    private Date editedAt;

    /**
     * Constructor for creating a new message.
     */
    public Message(UUID groupId, UserId senderId, String body) {
        this.groupId = validateGroupId(groupId);
        this.senderId = validateSenderId(senderId);
        this.body = validateAndSetBody(body);
        this.status = MessageStatus.SENT;
        this.sentAt = new Date();
        this.editedAt = null;
    }

    /**
     * Updates the message body content.
     * Only allowed if current status is SENT.
     */
    public void updateBody(String newBody) {
        if (this.status != MessageStatus.SENT) {
            throw new IllegalStateException("Cannot edit a message that is not in SENT status");
        }
        this.body = validateAndSetBody(newBody);
        this.status = MessageStatus.EDITED;
        this.editedAt = new Date();
    }

    /**
     * Updates the message status.
     */
    public void updateStatus(MessageStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Message status cannot be null");
        }
        this.status = newStatus;
        if (newStatus == MessageStatus.EDITED || newStatus == MessageStatus.DELETED) {
            this.editedAt = new Date();
        }
    }

    /**
     * Marks the message as edited.
     */
    public void markAsEdited() {
        this.status = MessageStatus.EDITED;
        this.editedAt = new Date();
    }

    /**
     * Marks the message as deleted.
     */
    public void markAsDeleted() {
        this.status = MessageStatus.DELETED;
        this.editedAt = new Date();
    }

    /**
     * Checks if the message can be edited.
     */
    public boolean canBeEdited() {
        return this.status == MessageStatus.SENT;
    }

    /**
     * Checks if the message is visible (not deleted).
     */
    public boolean isVisible() {
        return this.status != MessageStatus.DELETED;
    }

    /**
     * Checks if the message has been edited.
     */
    public boolean isEdited() {
        return this.status == MessageStatus.EDITED;
    }

    /**
     * Checks if the message belongs to the specified sender.
     */
    public boolean belongsToSender(UserId senderId) {
        return this.senderId.equals(senderId);
    }

    // Validation methods
    private UUID validateGroupId(UUID groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        return groupId;
    }

    private UserId validateSenderId(UserId senderId) {
        if (senderId == null) {
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        return senderId;
    }

    private String validateAndSetBody(String body) {
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Message body cannot be null or empty");
        }
        if (body.length() > 1000) {
            throw new IllegalArgumentException("Message body cannot exceed 1000 characters");
        }
        return body.trim();
    }
}
