package synera.centralis.api.chat.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.commands.CreateDirectConversationCommand;
import synera.centralis.api.chat.domain.model.commands.CreateMessageCommand;
import synera.centralis.api.chat.domain.model.entities.Message;
import synera.centralis.api.chat.domain.model.queries.GetGroupByIdQuery;
import synera.centralis.api.chat.domain.model.queries.GetGroupsByMemberIdQuery;
import synera.centralis.api.chat.domain.model.queries.GetMessagesByGroupIdQuery;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.chat.domain.services.GroupCommandService;
import synera.centralis.api.chat.domain.services.GroupQueryService;
import synera.centralis.api.chat.domain.services.MessageCommandService;
import synera.centralis.api.chat.domain.services.MessageQueryService;
import synera.centralis.api.chat.interfaces.rest.resources.ConversationResource;
import synera.centralis.api.chat.interfaces.rest.resources.CreateConversationResource;
import synera.centralis.api.chat.interfaces.rest.resources.CreateMessageResource;
import synera.centralis.api.chat.interfaces.rest.resources.MessageResource;
import synera.centralis.api.chat.interfaces.rest.transform.ConversationResourceFromEntityAssembler;
import synera.centralis.api.chat.interfaces.rest.transform.MessageResourceFromEntityAssembler;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;
import synera.centralis.api.shared.domain.exceptions.ResourceNotFoundException;
import synera.centralis.api.shared.domain.exceptions.UnauthorizedException;
import synera.centralis.api.shared.domain.exceptions.ValidationException;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.List;
import java.util.UUID;

/**
 * ConversationController — mensajería directa (1 a 1) estilo WhatsApp,
 * restringida a miembros de la misma compañía.
 *
 * <p>El cliente ve una API separada de los grupos. Internamente una
 * conversación directa es un grupo de tipo DIRECT, por lo que reutiliza
 * íntegramente la maquinaria de mensajes, WebSocket ({@code /ws-chat}) y
 * notificaciones push. El remitente se deriva SIEMPRE del JWT.</p>
 */
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET})
@RestController
@RequestMapping(value = "/api/v1/conversations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Direct Conversations", description = "Direct (1-to-1) messaging endpoints")
public class ConversationController {

    private final GroupCommandService groupCommandService;
    private final GroupQueryService groupQueryService;
    private final MessageCommandService messageCommandService;
    private final MessageQueryService messageQueryService;
    private final IamContextFacade iamContextFacade;

    public ConversationController(GroupCommandService groupCommandService,
                                  GroupQueryService groupQueryService,
                                  MessageCommandService messageCommandService,
                                  MessageQueryService messageQueryService,
                                  IamContextFacade iamContextFacade) {
        this.groupCommandService = groupCommandService;
        this.groupQueryService = groupQueryService;
        this.messageCommandService = messageCommandService;
        this.messageQueryService = messageQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    private String getAuthenticatedUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new UnauthorizedException("Authentication required");
        }
        return authentication.getName();
    }

    private UUID getCurrentUserId() {
        UUID userId = iamContextFacade.fetchUserIdByUsername(getAuthenticatedUsername());
        if (userId == null) {
            throw new UnauthorizedException("Authenticated user not found");
        }
        return userId;
    }

    private CompanyId getCurrentCompanyId() {
        UUID companyId = iamContextFacade.fetchCompanyIdByUsername(getAuthenticatedUsername());
        if (companyId == null) {
            throw new UnauthorizedException("User not associated with any company");
        }
        return new CompanyId(companyId);
    }

    /**
     * Carga una conversación directa validando compañía, tipo DIRECT y que el
     * usuario autenticado sea participante.
     */
    private Group requireDirectConversation(UUID conversationId, UUID currentUserId, CompanyId companyId) {
        var group = groupQueryService.handle(new GetGroupByIdQuery(conversationId, companyId))
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        if (!group.isDirect()) {
            throw new ResourceNotFoundException("Conversation not found");
        }
        if (!group.isMember(new UserId(currentUserId))) {
            throw new UnauthorizedException("You are not a participant of this conversation");
        }
        return group;
    }

    /**
     * Abre o recupera la conversación directa con otro miembro de la compañía.
     */
    @PostMapping
    @Operation(summary = "Open a direct conversation",
            description = "Gets or creates the 1-to-1 conversation between the authenticated user and the target user (same company)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversation opened"),
            @ApiResponse(responseCode = "400", description = "Invalid request or different company"),
            @ApiResponse(responseCode = "401", description = "Not authenticated / no company")
    })
    public ResponseEntity<ConversationResource> openConversation(
            @Valid @RequestBody CreateConversationResource resource) {
        UUID currentUserId = getCurrentUserId();
        CompanyId companyId = getCurrentCompanyId();

        UUID targetUserId = resource.targetUserId();
        if (targetUserId.equals(currentUserId)) {
            throw new ValidationException("Cannot start a direct conversation with yourself");
        }

        UUID targetCompanyId = iamContextFacade.fetchCompanyIdByUserId(targetUserId);
        if (targetCompanyId == null || !targetCompanyId.equals(companyId.companyId())) {
            throw new ValidationException("Target user is not a member of your company");
        }

        var command = new CreateDirectConversationCommand(
                new UserId(currentUserId), new UserId(targetUserId), companyId);
        var conversation = groupCommandService.handle(command);

        var conversationResource =
                ConversationResourceFromEntityAssembler.toResourceFromEntity(conversation, currentUserId);
        return new ResponseEntity<>(conversationResource, HttpStatus.CREATED);
    }

    /**
     * Lista las conversaciones directas del usuario autenticado.
     */
    @GetMapping
    @Operation(summary = "List my direct conversations",
            description = "Returns all 1-to-1 conversations the authenticated user participates in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversations retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated / no company")
    })
    public ResponseEntity<List<ConversationResource>> getMyConversations() {
        UUID currentUserId = getCurrentUserId();
        CompanyId companyId = getCurrentCompanyId();

        var query = new GetGroupsByMemberIdQuery(new UserId(currentUserId), companyId);
        var conversations = groupQueryService.handle(query).stream()
                .filter(Group::isDirect)
                .map(g -> ConversationResourceFromEntityAssembler.toResourceFromEntity(g, currentUserId))
                .toList();

        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    /**
     * Lista los mensajes de una conversación directa.
     */
    @GetMapping("/{conversationId}/messages")
    @Operation(summary = "Get conversation messages",
            description = "Retrieves all messages in the specified direct conversation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved"),
            @ApiResponse(responseCode = "401", description = "Not a participant / no company"),
            @ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    public ResponseEntity<List<MessageResource>> getConversationMessages(
            @Parameter(description = "Conversation ID", required = true) @PathVariable UUID conversationId) {
        UUID currentUserId = getCurrentUserId();
        CompanyId companyId = getCurrentCompanyId();
        requireDirectConversation(conversationId, currentUserId, companyId);

        var query = new GetMessagesByGroupIdQuery(conversationId, companyId);
        List<Message> messages = messageQueryService.handle(query);
        var messageResources = messages.stream()
                .map(MessageResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new ResponseEntity<>(messageResources, HttpStatus.OK);
    }

    /**
     * Envía un mensaje en una conversación directa. El remitente se deriva del
     * JWT (el {@code senderId} del cuerpo se ignora). Reutiliza la lógica de
     * mensajería de grupos: evento de dominio, broadcast WebSocket y push.
     */
    @PostMapping("/{conversationId}/messages")
    @Operation(summary = "Send a direct message",
            description = "Sends a message in the direct conversation. Sender is taken from the JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message sent"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Not a participant / no company"),
            @ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    public ResponseEntity<MessageResource> sendDirectMessage(
            @Parameter(description = "Conversation ID", required = true) @PathVariable UUID conversationId,
            @Valid @RequestBody CreateMessageResource resource) {
        UUID currentUserId = getCurrentUserId();
        CompanyId companyId = getCurrentCompanyId();
        requireDirectConversation(conversationId, currentUserId, companyId);

        var command = new CreateMessageCommand(
                conversationId, new UserId(currentUserId), resource.body());
        var message = messageCommandService.handle(command);

        var messageResource = MessageResourceFromEntityAssembler.toResourceFromEntity(message);
        return new ResponseEntity<>(messageResource, HttpStatus.CREATED);
    }
}
