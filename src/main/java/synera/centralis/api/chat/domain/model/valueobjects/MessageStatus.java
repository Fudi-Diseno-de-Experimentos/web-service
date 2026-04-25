package synera.centralis.api.chat.domain.model.valueobjects;

/**
 * Enum representing the status of a message in the chat system.
 * Defines the different states a message can have throughout its lifecycle.
 */
public enum MessageStatus {
    /**
     * Message has been sent and is visible to all group members
     */
    SENT,
    
    /**
     * Message content has been modified by the sender
     */
    EDITED,
    
    /**
     * Message has been deleted and should not be visible
     */
    DELETED
}