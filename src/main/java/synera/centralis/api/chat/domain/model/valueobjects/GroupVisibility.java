package synera.centralis.api.chat.domain.model.valueobjects;

/**
 * Enum representing the visibility level of a group in the chat system.
 * Defines who can see and join the group.
 */
public enum GroupVisibility {
    /**
     * Group is visible to all users and can be joined freely
     */
    PUBLIC,
    
    /**
     * Group is only visible to invited members
     */
    PRIVATE
}