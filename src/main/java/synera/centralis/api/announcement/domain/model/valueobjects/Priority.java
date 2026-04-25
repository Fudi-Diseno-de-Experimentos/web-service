package synera.centralis.api.announcement.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Priority Value Object
 * Represents the priority level of an announcement
 */
@Embeddable
public record Priority(@Enumerated(EnumType.STRING) PriorityLevel level) {
    
    public Priority {
        if (level == null) {
            throw new IllegalArgumentException("Priority level cannot be null");
        }
    }
    
    public static Priority normal() {
        return new Priority(PriorityLevel.NORMAL);
    }
    
    public static Priority high() {
        return new Priority(PriorityLevel.HIGH);
    }
    
    public static Priority urgent() {
        return new Priority(PriorityLevel.URGENT);
    }
    
    public boolean isUrgent() {
        return level == PriorityLevel.URGENT;
    }
    
    public boolean isHighOrUrgent() {
        return level == PriorityLevel.HIGH || level == PriorityLevel.URGENT;
    }
    
    /**
     * Priority Level Enumeration
     */
    public enum PriorityLevel {
        NORMAL("Normal"),
        HIGH("High"),
        URGENT("Urgent");
        
        private final String displayName;
        
        PriorityLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}