package synera.centralis.api.chat.application.internal.eventhandlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import synera.centralis.api.chat.domain.model.queries.GetMessageByIdQuery;
import synera.centralis.api.chat.domain.services.MessageQueryService;
import synera.centralis.api.chat.interfaces.rest.resources.MessageResource;
import synera.centralis.api.chat.interfaces.rest.transform.MessageResourceFromEntityAssembler;
import synera.centralis.api.shared.domain.events.MessageSentInGroupEvent;

/**
 * Difunde por WebSocket/STOMP los mensajes recién creados.
 * <p>
 * Escucha {@link MessageSentInGroupEvent} (publicado por
 * {@code MessageCommandServiceImpl}) tras el commit de la transacción, de modo
 * que un mensaje creado por REST o por WebSocket se emite de forma idéntica a
 * {@code /topic/group.{groupId}}. Esto elimina la divergencia previa en la que
 * solo el flujo WebSocket difundía en tiempo real.
 * </p>
 */
@Slf4j
@Component
public class MessageBroadcastHandler {

    private final MessageQueryService messageQueryService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageBroadcastHandler(
            MessageQueryService messageQueryService,
            SimpMessagingTemplate messagingTemplate) {
        this.messageQueryService = messageQueryService;
        this.messagingTemplate = messagingTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(MessageSentInGroupEvent event) {
        var message = messageQueryService.handle(new GetMessageByIdQuery(event.messageId()));
        if (message.isEmpty()) {
            log.warn("[WebSocket] Mensaje {} no encontrado para broadcast", event.messageId());
            return;
        }

        MessageResource resource = MessageResourceFromEntityAssembler.toResourceFromEntity(message.get());
        String destination = "/topic/group." + event.groupId();
        messagingTemplate.convertAndSend(destination, resource);
        log.info("[WebSocket] Broadcast enviado a {} (mensaje {})", destination, event.messageId());
    }
}
