package synera.centralis.api.notification.domain.model.valueobjects;

/**
 * Represents the type/category of a notification.
 * Defines different kinds of notifications that can be sent to users.
 */
public enum NotificationType {
    
    // General notifications
    GENERAL("General notification"),
    
    // Announcement related
    ANNOUNCEMENT("Announcement notification"),
    URGENT_ANNOUNCEMENT("Urgent announcement notification"),
    
    // Chat related
    GROUP_MESSAGE("Group message notification"),
    GROUP_CREATED("Group created notification"),
    
    // System notifications
    SYSTEM("System notification"),
    REMINDER("Reminder notification");
    
    private final String description;
    
    NotificationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this notification type is urgent/high priority.
     */
    public boolean isUrgent() {
        return this == URGENT_ANNOUNCEMENT || this == SYSTEM;
    }
    
    /**
     * Check if this notification type is related to chat functionality.
     */
    public boolean isChatRelated() {
        return this == GROUP_MESSAGE || this == GROUP_CREATED;
    }
}