package synera.centralis.api.shared.infrastructure.eventlisteners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import synera.centralis.api.shared.domain.events.GroupCreatedEvent;
import synera.centralis.api.shared.domain.events.MessageSentInGroupEvent;
import synera.centralis.api.shared.domain.events.UrgentAnnouncementCreatedEvent;

import java.util.logging.Logger;

/**
 * Debug event listener to verify that events are being published correctly.
 * This listener will log all domain events for debugging purposes.
 */
@Component
public class DebugEventListener {
    
    private static final Logger logger = Logger.getLogger(DebugEventListener.class.getName());
    
    @EventListener
    public void handle(UrgentAnnouncementCreatedEvent event) {
        logger.info("🔍 DEBUG: UrgentAnnouncementCreatedEvent received!");
        logger.info("   - Event ID: " + event.eventId());
        logger.info("   - Title: " + event.title());
        logger.info("   - Content: " + event.content());
        logger.info("   - Created By: " + event.createdBy());
        logger.info("   - Occurred At: " + event.occurredAt());
    }
    
    @EventListener
    public void handle(GroupCreatedEvent event) {
        logger.info("🔍 DEBUG: GroupCreatedEvent received!");
        logger.info("   - Event ID: " + event.eventId());
        logger.info("   - Group ID: " + event.groupId());
        logger.info("   - Group Name: " + event.groupName());
        logger.info("   - Created By: " + event.createdBy());
        logger.info("   - Member IDs: " + event.memberIds());
        logger.info("   - Occurred At: " + event.occurredAt());
    }
    
    @EventListener
    public void handle(MessageSentInGroupEvent event) {
        logger.info("🔍 DEBUG: MessageSentInGroupEvent received!");
        logger.info("   - Event ID: " + event.eventId());
        logger.info("   - Message ID: " + event.messageId());
        logger.info("   - Group ID: " + event.groupId());
        logger.info("   - Group Name: " + event.groupName());
        logger.info("   - Sender ID: " + event.senderId());
        logger.info("   - Message Content: " + event.messageContent());
        logger.info("   - Occurred At: " + event.occurredAt());
    }
}