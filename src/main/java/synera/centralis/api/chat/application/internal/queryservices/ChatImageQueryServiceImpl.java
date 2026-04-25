package synera.centralis.api.chat.application.internal.queryservices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.chat.domain.model.entities.ChatImage;
import synera.centralis.api.chat.domain.model.queries.GetChatImageByIdQuery;
import synera.centralis.api.chat.domain.model.queries.GetChatImagesByGroupIdQuery;
import synera.centralis.api.chat.domain.services.ChatImageQueryService;
import synera.centralis.api.chat.infrastructure.persistence.jpa.repositories.ChatImageRepository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ChatImageQueryService.
 * Handles all chat image query operations with proper transaction management.
 */
@Service
public class ChatImageQueryServiceImpl implements ChatImageQueryService {

    private final ChatImageRepository chatImageRepository;

    public ChatImageQueryServiceImpl(ChatImageRepository chatImageRepository) {
        this.chatImageRepository = chatImageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatImage> handle(GetChatImageByIdQuery query) {
        try {
            return chatImageRepository.findVisibleByImageId(query.imageId());
        } catch (Exception e) {
            System.err.println("Error retrieving chat image by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatImage> handle(GetChatImagesByGroupIdQuery query) {
        try {
            return chatImageRepository.findVisibleByGroupIdOrderBySentAtDesc(query.groupId());
        } catch (Exception e) {
            System.err.println("Error retrieving chat images by group ID: " + e.getMessage());
            return List.of();
        }
    }
}