package synera.centralis.api.chat.application.internal.commandservices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.chat.domain.model.commands.CreateChatImageCommand;
import synera.centralis.api.chat.domain.model.commands.DeleteChatImageCommand;
import synera.centralis.api.chat.domain.model.entities.ChatImage;
import synera.centralis.api.chat.domain.services.ChatImageCommandService;
import synera.centralis.api.chat.infrastructure.persistence.jpa.repositories.ChatImageRepository;

import java.util.Optional;

/**
 * Implementation of ChatImageCommandService.
 * Handles all chat image command operations with proper transaction management.
 */
@Service
public class ChatImageCommandServiceImpl implements ChatImageCommandService {

    private final ChatImageRepository chatImageRepository;

    public ChatImageCommandServiceImpl(ChatImageRepository chatImageRepository) {
        this.chatImageRepository = chatImageRepository;
    }

    @Override
    @Transactional
    public Optional<ChatImage> handle(CreateChatImageCommand command) {
        try {
            var chatImage = new ChatImage(
                command.groupId(),
                command.senderId(),
                command.imageUrl()
            );
            
            var savedChatImage = chatImageRepository.save(chatImage);
            return Optional.of(savedChatImage);
        } catch (Exception e) {
            System.err.println("Error creating chat image: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<ChatImage> handle(DeleteChatImageCommand command) {
        try {
            Optional<ChatImage> chatImageOpt = chatImageRepository.findById(command.imageId());
            
            if (chatImageOpt.isEmpty()) {
                return Optional.empty();
            }
            
            var chatImage = chatImageOpt.get();
            chatImage.delete(); // Soft delete
            
            var savedChatImage = chatImageRepository.save(chatImage);
            return Optional.of(savedChatImage);
        } catch (Exception e) {
            System.err.println("Error deleting chat image: " + e.getMessage());
            return Optional.empty();
        }
    }
}