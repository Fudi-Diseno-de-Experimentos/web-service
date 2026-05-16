package synera.centralis.api.shared.infrastructure.eventlisteners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import synera.centralis.api.shared.domain.events.GroupCreatedEvent;
import synera.centralis.api.shared.domain.events.MessageSentInGroupEvent;
import synera.centralis.api.shared.domain.events.UrgentAnnouncementCreatedEvent;

/**
 * Debug event listener to verify that domain events are published.
 * <p>
 * Runs after commit on the async event executor so it never adds latency to
 * the publishing transaction, and logs identifiers only — message bodies,
 * announcement content and member lists are deliberately omitted to keep
 * user content out of the logs.
 * </p>
 */
@Slf4j
@Component
public class DebugEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskExecutor")
    public void handle(UrgentAnnouncementCreatedEvent event) {
        log.info("DEBUG: UrgentAnnouncementCreatedEvent — eventId=" + event.eventId()
                + ", createdBy=" + event.createdBy()
                + ", occurredAt=" + event.occurredAt());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskExecutor")
    public void handle(GroupCreatedEvent event) {
        log.info("DEBUG: GroupCreatedEvent — eventId=" + event.eventId()
                + ", groupId=" + event.groupId()
                + ", createdBy=" + event.createdBy()
                + ", occurredAt=" + event.occurredAt());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskExecutor")
    public void handle(MessageSentInGroupEvent event) {
        log.info("DEBUG: MessageSentInGroupEvent — eventId=" + event.eventId()
                + ", messageId=" + event.messageId()
                + ", groupId=" + event.groupId()
                + ", senderId=" + event.senderId()
                + ", occurredAt=" + event.occurredAt());
    }
}
