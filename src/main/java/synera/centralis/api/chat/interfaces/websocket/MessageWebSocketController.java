package synera.centralis.api.chat.interfaces.websocket;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import synera.centralis.api.chat.domain.model.commands.CreateMessageCommand;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.chat.domain.services.MessageCommandService;
import synera.centralis.api.chat.interfaces.websocket.resources.SendMessageWsPayload;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;
import synera.centralis.api.shared.domain.exceptions.ValidationException;
import synera.centralis.api.shared.interfaces.rest.resources.ErrorResource;

import java.security.Principal;
import java.util.UUID;

/**
 * Controlador WebSocket/STOMP para el envío de mensajes en tiempo real.
 *
 * <h2>Flujo de mensaje</h2>
 * <ol>
 *   <li>El cliente Flutter se conecta a {@code /ws-chat} con JWT en el header STOMP CONNECT.</li>
 *   <li>El cliente publica un payload JSON al destino {@code /app/chat.send/{groupId}}.</li>
 *   <li>Este controlador crea el mensaje en la base de datos
 *       (reutilizando {@link MessageCommandService}).</li>
 *   <li>{@code MessageCommandServiceImpl} publica {@code MessageSentInGroupEvent}; el
 *       broadcast a {@code /topic/group.{groupId}} lo realiza
 *       {@code MessageBroadcastHandler} tras el commit, de modo que los mensajes
 *       creados vía REST y vía WebSocket se difunden de forma idéntica.</li>
 * </ol>
 *
 * <h2>Destinos STOMP</h2>
 * <ul>
 *   <li><b>Publicar (cliente → servidor):</b> {@code /app/chat.send/{groupId}}</li>
 *   <li><b>Suscribirse (servidor → cliente):</b> {@code /topic/group.{groupId}}</li>
 *   <li><b>Errores (servidor → cliente):</b> {@code /user/queue/errors}</li>
 * </ul>
 *
 * <h2>Formato del payload de entrada</h2>
 * <pre>
 * {
 *   "senderId": "uuid-del-remitente",   // ignorado: el remitente se deriva del JWT
 *   "body": "Texto del mensaje"
 * }
 * </pre>
 */
@Slf4j
@Controller
public class MessageWebSocketController {

    private final MessageCommandService messageCommandService;
    private final IamContextFacade iamContextFacade;

    public MessageWebSocketController(
            MessageCommandService messageCommandService,
            IamContextFacade iamContextFacade) {
        this.messageCommandService = messageCommandService;
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Recibe un mensaje de un cliente autenticado y lo persiste.
     * El broadcast en tiempo real lo gestiona {@code MessageBroadcastHandler}
     * al consumir el evento de dominio.
     *
     * @param groupId   UUID del grupo destino (desde la ruta del destino STOMP)
     * @param payload   Cuerpo del mensaje enviado por el cliente
     * @param principal Usuario autenticado (establecido por {@code WebSocketAuthChannelInterceptor})
     */
    @MessageMapping("/chat.send/{groupId}")
    public void sendMessage(
            @DestinationVariable UUID groupId,
            @Valid @Payload SendMessageWsPayload payload,
            Principal principal) {

        if (principal == null) {
            log.warn("[WebSocket] Mensaje recibido sin principal autenticado — rechazado");
            throw new UnauthorizedException("Autenticación requerida para enviar mensajes");
        }

        // El remitente se deriva SIEMPRE del usuario autenticado, nunca del payload,
        // para impedir suplantación. payload.senderId() se ignora deliberadamente.
        UUID authenticatedUserId = iamContextFacade.fetchUserIdByUsername(principal.getName());
        if (authenticatedUserId == null) {
            log.warn("[WebSocket] No se pudo resolver el usuario autenticado: {}", principal.getName());
            throw new UnauthorizedException("Usuario autenticado no encontrado");
        }

        log.info("[WebSocket] Mensaje recibido — grupo: {}, remitente autenticado: {}",
                groupId, authenticatedUserId);

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
                new UserId(authenticatedUserId),
                payload.body()
        );

        var savedMessage = messageCommandService.handle(command);
        log.info("[WebSocket] Mensaje guardado con ID: {}", savedMessage.getMessageId());
    }

    /**
     * Devuelve los errores de procesamiento al cliente que originó el mensaje,
     * en su cola privada {@code /user/queue/errors}, en lugar de descartarlos
     * silenciosamente en el canal del broker.
     */
    @MessageExceptionHandler
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public ErrorResource handleException(Exception exception) {
        int status = switch (exception) {
            case UnauthorizedException ignored -> 401;
            case ValidationException ignored -> 400;
            default -> 400;
        };
        log.warn("[WebSocket] Error procesando mensaje ({}): {}", status, exception.getMessage());
        return new ErrorResource(
                exception.getMessage(),
                exception.getClass().getSimpleName(),
                status
        );
    }
}
