package synera.centralis.api.announcement.application.internal.commandservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.announcement.domain.model.commands.AddCommentToAnnouncementCommand;
import synera.centralis.api.announcement.domain.model.commands.DeleteCommentCommand;
import synera.centralis.api.announcement.domain.model.entities.Comment;
import synera.centralis.api.announcement.domain.services.AnnouncementCommandService;
import synera.centralis.api.announcement.domain.services.CommentCommandService;
import synera.centralis.api.announcement.infrastructure.persistence.jpa.repositories.CommentRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Comment Command Service Implementation
 * Handles command operations for comments
 */
@Service
public class CommentCommandServiceImpl implements CommentCommandService {

    private final CommentRepository commentRepository;
    private final AnnouncementCommandService announcementCommandService;

    @Autowired
    public CommentCommandServiceImpl(CommentRepository commentRepository,
                                   AnnouncementCommandService announcementCommandService) {
        this.commentRepository = commentRepository;
        this.announcementCommandService = announcementCommandService;
    }

    @Override
    @Transactional
    public Optional<Comment> handle(AddCommentToAnnouncementCommand command) {
        // Verify that the announcement exists
        if (!announcementCommandService.existsById(command.announcementId())) {
            throw new IllegalArgumentException("Announcement with ID " + command.announcementId() + " does not exist");
        }

        try {
            var comment = new Comment(
                command.announcementId(),
                command.employeeId(),
                command.content()
            );

            var savedComment = commentRepository.save(comment);
            return Optional.of(savedComment);
        } catch (Exception e) {
            throw new RuntimeException("Error creating comment: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean handle(DeleteCommentCommand command) {
        return commentRepository.findById(command.commentId())
                .map(comment -> {
                    commentRepository.delete(comment);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID commentId) {
        return commentRepository.existsById(commentId);
    }
}