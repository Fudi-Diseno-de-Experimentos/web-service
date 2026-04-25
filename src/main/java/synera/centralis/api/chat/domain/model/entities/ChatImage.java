package synera.centralis.api.chat.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;

import java.util.Date;
import java.util.UUID;

/**
 * ChatImage entity representing an image shared within a group chat.
 * Contains image URL and metadata.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "chat_images")
public class ChatImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "image_id")
    private UUID imageId;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "sender_id"))
    private UserId senderId;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_at", nullable = false)
    private Date sentAt;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible;

    /**
     * Constructor for creating a new chat image.
     */
    public ChatImage(UUID groupId, UserId senderId, String imageUrl) {
        this.groupId = validateGroupId(groupId);
        this.senderId = validateSenderId(senderId);
        this.imageUrl = validateAndSetImageUrl(imageUrl);
        this.sentAt = new Date();
        this.isVisible = true;
    }

    /**
     * Marks the image as deleted (soft delete).
     */
    public void delete() {
        this.isVisible = false;
    }

    /**
     * Restores the image (undoes soft delete).
     */
    public void restore() {
        this.isVisible = true;
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

    private String validateAndSetImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        if (imageUrl.length() > 500) {
            throw new IllegalArgumentException("Image URL cannot exceed 500 characters");
        }
        // Basic URL validation
        String trimmedUrl = imageUrl.trim();
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Image URL must be a valid HTTP/HTTPS URL");
        }
        return trimmedUrl;
    }
}