package synera.centralis.api.chat.interfaces.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import synera.centralis.api.chat.domain.model.commands.CreateMessageCommand;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.chat.domain.services.MessageCommandService;
import synera.centralis.api.chat.interfaces.rest.resources.MessageResource;
import synera.centralis.api.chat.interfaces.rest.transform.MessageResourceFromEntityAssembler;
import synera.centralis.api.chat.interfaces.websocket.resources.SendMessageWsPayload;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;

import java.security.Principal;
import java.util.UUID;

/**
 * Controlador WebSocket/STOMP para el envío de mensajes en tiempo real.
 *
 * <h2>Flujo de mensaje</h2>
 * <ol>
 *   <li>El cliente Flutter se conecta a {@code /ws-chat} con JWT en el header STOMP CONNECT.</li>
 *   <li>El cliente publica un payload JSON al destino {@code /app/chat.send/{groupId}}.</li>
 *   <li>Este controlador recibe el payload, crea el mensaje en la base de datos
 *       (reutilizando {@link MessageCommandService}).</li>
 *   <li>El mensaje guardado se emite a {@code /topic/group.{groupId}}.</li>
 *   <li>Todos los clientes suscritos a ese topic reciben el mensaje en tiempo real.</li>
 * </ol>
 *
 * <h2>Destinos STOMP</h2>
 * <ul>
 *   <li><b>Publicar (cliente → servidor):</b> {@code /app/chat.send/{groupId}}</li>
 *   <li><b>Suscribirse (servidor → cliente):</b> {@code /topic/group.{groupId}}</li>
 * </ul>
 *
 * <h2>Formato del payload de entrada</h2>
 * <pre>
 * {
 *   "senderId": "uuid-del-remitente",
 *   "body": "Texto del mensaje"
 * }
 * </pre>
 *
 * <h2>Formato del payload de salida (broadcast)</h2>
 * El servidor emite un {@link MessageResource} con todos los campos del mensaje guardado.
 */
@Slf4j
@Controller
public class MessageWebSocketController {

    private final MessageCommandService messageCommandService;
    private final SimpMessagingTemplate messagingTemplate;
    private final IamContextFacade iamContextFacade;

    public MessageWebSocketController(
            MessageCommandService messageCommandService,
            SimpMessagingTemplate messagingTemplate,
            IamContextFacade iamContextFacade) {
        this.messageCommandService = messageCommandService;
        this.messagingTemplate = messagingTemplate;
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Recibe un mensaje de un cliente autenticado y lo reenvía a todos los miembros del grupo.
     *
     * @param groupId   UUID del grupo destino (desde la ruta del destino STOMP)
     * @param payload   Cuerpo del mensaje enviado por el cliente
     * @param principal Usuario autenticado (establecido por {@code WebSocketAuthChannelInterceptor})
     */
    @MessageMapping("/chat.send/{groupId}")
    public void sendMessage(
            @DestinationVariable UUID groupId,
            @Payload SendMessageWsPayload payload,
            Principal principal) {

        log.info("[WebSocket] Mensaje recibido — grupo: {}, remitente: {}, longitud: {} chars",
                groupId, payload.senderId(), payload.body().length());

        // Verificar que el usuario autenticado está asociado a una compañía
        UUID companyId = iamContextFacade.fetchCompanyIdByUsername(principal.getName());
        if (companyId == null) {
            log.warn("[WebSocket] Usuario {} no está asociado a ninguna compañía", principal.getName());
            throw new UnauthorizedException("El usuario no está asociado a ninguna compañía");
        }

        // Crear el comando — reutiliza toda la lógica de negocio existente:
        // validación de membresía, publicación del evento MessageSentInGroupEvent,
        // y notificaciones push vía GroupMessageNotificationHandler
        var command = new CreateMessageCommand(
                groupId,
                new UserId(payload.senderId()),
                payload.body()
        );

        var savedMessage = messageCommandService.handle(command);
        log.info("[WebSocket] Mensaje guardado con ID: {}", savedMessage.getMessageId());

        // Construir el resource de respuesta (mismo formato que el REST controller)
        MessageResource response = MessageResourceFromEntityAssembler.toResourceFromEntity(savedMessage);

        // Broadcast a TODOS los clientes suscritos al topic del grupo
        String topicDestination = "/topic/group." + groupId;
        messagingTemplate.convertAndSend(topicDestination, response);

        log.info("[WebSocket] Mensaje broadcast enviado a topic: {}", topicDestination);
    }
}
