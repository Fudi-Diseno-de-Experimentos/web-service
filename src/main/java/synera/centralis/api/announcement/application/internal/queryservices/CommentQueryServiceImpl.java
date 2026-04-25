package synera.centralis.api.announcement.application.internal.queryservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.announcement.domain.model.entities.Comment;
import synera.centralis.api.announcement.domain.model.queries.GetCommentByIdQuery;
import synera.centralis.api.announcement.domain.model.queries.GetCommentsByAnnouncementQuery;
import synera.centralis.api.announcement.domain.services.CommentQueryService;
import synera.centralis.api.announcement.infrastructure.persistence.jpa.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

/**
 * Comment Query Service Implementation
 * Handles query operations for comments
 */
@Service
public class CommentQueryServiceImpl implements CommentQueryService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentQueryServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> handle(GetCommentByIdQuery query) {
        return commentRepository.findById(query.commentId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> handle(GetCommentsByAnnouncementQuery query) {
        return commentRepository.findByAnnouncementIdOrderByCreatedAtAsc(query.announcementId());
    }
}