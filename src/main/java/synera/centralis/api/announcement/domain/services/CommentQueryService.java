package synera.centralis.api.announcement.domain.services;

import synera.centralis.api.announcement.domain.model.entities.Comment;
import synera.centralis.api.announcement.domain.model.queries.GetCommentByIdQuery;
import synera.centralis.api.announcement.domain.model.queries.GetCommentsByAnnouncementQuery;

import java.util.List;
import java.util.Optional;

/**
 * Comment Query Service
 * Defines the contract for comment query operations
 */
public interface CommentQueryService {

    /**
     * Handles retrieving a comment by ID
     */
    Optional<Comment> handle(GetCommentByIdQuery query);

    /**
     * Handles retrieving comments by announcement
     */
    List<Comment> handle(GetCommentsByAnnouncementQuery query);
}