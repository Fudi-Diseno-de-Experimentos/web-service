package synera.centralis.api.chat.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Group aggregate root representing a chat group.
 * Contains group information and member management capabilities.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "chat_groups")
public class Group extends AuditableAbstractAggregateRoot<Group> {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private GroupVisibility visibility;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "created_by"))
    private UserId createdBy;

    @ElementCollection
    @CollectionTable(name = "group_members", joinColumns = @JoinColumn(name = "group_id"))
    @AttributeOverride(name = "userId", column = @Column(name = "user_id"))
    private Set<UserId> members = new HashSet<>();

    /**
     * Constructor for creating a new group.
     */
    public Group(String name, String description, String imageUrl, GroupVisibility visibility, 
                 List<UUID> memberIds, UserId createdBy) {
        this.name = validateAndSetName(name);
        this.description = validateAndSetDescription(description);
        this.imageUrl = validateAndSetImageUrl(imageUrl);
        this.visibility = validateVisibility(visibility);
        this.createdBy = validateCreatedBy(createdBy);
        this.members = new HashSet<>();
        
        // Add creator as member
        this.members.add(createdBy);
        
        // Add other members
        if (memberIds != null) {
            memberIds.forEach(memberId -> this.members.add(new UserId(memberId)));
        }
        
        validateAtLeastOneMember();
    }

    /**
     * Updates group information (name, description, image).
     */
    public void updateGroup(String name, String description, String imageUrl) {
        if (name != null) {
            this.name = validateAndSetName(name);
        }
        if (description != null) {
            this.description = validateAndSetDescription(description);
        }
        if (imageUrl != null) {
            this.imageUrl = validateAndSetImageUrl(imageUrl);
        }
    }

    /**
     * Updates group visibility.
     */
    public void updateVisibility(GroupVisibility visibility) {
        this.visibility = validateVisibility(visibility);
    }

    /**
     * Adds a new member to the group.
     */
    public void addMember(UserId memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
        if (this.members.contains(memberId)) {
            throw new IllegalArgumentException("User is already a member of this group");
        }
        this.members.add(memberId);
    }

    /**
     * Removes a member from the group.
     */
    public void removeMember(UserId memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
        if (!this.members.contains(memberId)) {
            throw new IllegalArgumentException("User is not a member of this group");
        }
        if (this.members.size() <= 1) {
            throw new IllegalArgumentException("Cannot remove the last member from the group");
        }
        this.members.remove(memberId);
    }

    /**
     * Checks if a user is a member of this group.
     */
    public boolean isMember(UserId userId) {
        return this.members.contains(userId);
    }

    /**
     * Gets the number of members in the group.
     */
    public int getMemberCount() {
        return this.members.size();
    }

    // Validation methods
    private String validateAndSetName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Group name cannot exceed 100 characters");
        }
        return name.trim();
    }

    private String validateAndSetDescription(String description) {
        if (description != null) {
            if (description.length() > 500) {
                throw new IllegalArgumentException("Group description cannot exceed 500 characters");
            }
            return description.trim().isEmpty() ? null : description.trim();
        }
        return null;
    }

    private String validateAndSetImageUrl(String imageUrl) {
        if (imageUrl != null) {
            if (imageUrl.length() > 500) {
                throw new IllegalArgumentException("Image URL cannot exceed 500 characters");
            }
            return imageUrl.trim().isEmpty() ? null : imageUrl.trim();
        }
        return null;
    }

    private GroupVisibility validateVisibility(GroupVisibility visibility) {
        if (visibility == null) {
            throw new IllegalArgumentException("Group visibility cannot be null");
        }
        return visibility;
    }

    private UserId validateCreatedBy(UserId createdBy) {
        if (createdBy == null) {
            throw new IllegalArgumentException("Created by cannot be null");
        }
        return createdBy;
    }

    private void validateAtLeastOneMember() {
        if (this.members.isEmpty()) {
            throw new IllegalArgumentException("Group must have at least one member");
        }
    }
}
