package synera.centralis.api.announcement.domain.services;

import synera.centralis.api.announcement.domain.model.commands.AddCommentToAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.commands.DeleteCommentCommand;
import synera.centralis.api.announcement.domain.model.entities.Comment;

import java.util.Optional;
import java.util.UUID;

/**
 * Comment Command Service
 * Defines the contract for comment command operations
 */
public interface CommentCommandService {

    /**
     * Handles adding a comment to an announcement
     */
    Optional<Comment> handle(AddCommentToAnnouncementCommand command);

    /**
     * Handles the deletion of a comment
     */
    boolean handle(DeleteCommentCommand command);

    /**
     * Checks if a comment exists by ID
     */
    boolean existsById(UUID commentId);
}