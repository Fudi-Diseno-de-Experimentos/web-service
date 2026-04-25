package synera.centralis.api.announcement.domain.model.commands;

import java.util.UUID;

/**
 * Delete Comment Command
 * Represents the intention to delete a comment
 */
public record DeleteCommentCommand(UUID commentId) {
    public DeleteCommentCommand {
        if (commentId == null) {
            throw new IllegalArgumentException("Comment ID cannot be null");
        }
    }
}