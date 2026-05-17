package synera.centralis.api.tests.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.commands.CreateDirectConversationCommand;
import synera.centralis.api.chat.domain.model.valueobjects.GroupType;
import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.chat.interfaces.rest.resources.ConversationResource;
import synera.centralis.api.chat.interfaces.rest.resources.GroupResource;
import synera.centralis.api.chat.interfaces.rest.transform.ConversationResourceFromEntityAssembler;
import synera.centralis.api.chat.interfaces.rest.transform.GroupResourceFromEntityAssembler;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de la mensajería directa (1 a 1) estilo WhatsApp.
 * Cubre el comando, la fábrica del agregado, la separación grupo/conversación
 * y los assemblers de recurso. Sin contexto de Spring (objetos reales, patrón AAA).
 */
class DirectConversationUnitTests {

    private final CompanyId companyId = new CompanyId(UUID.randomUUID());
    private final UserId requester = new UserId(UUID.randomUUID());
    private final UserId target = new UserId(UUID.randomUUID());

    // --- CreateDirectConversationCommand ---

    @Test
    @DisplayName("Comando válido de conversación directa se construye correctamente")
    void shouldCreateCommand_WhenValid() {
        var command = new CreateDirectConversationCommand(requester, target, companyId);

        assertEquals(requester, command.requester());
        assertEquals(target, command.target());
        assertEquals(companyId, command.companyId());
    }

    @Test
    @DisplayName("Falla el comando si el solicitante es nulo")
    void shouldFailCommand_WhenRequesterNull() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new CreateDirectConversationCommand(null, target, companyId));
        assertTrue(ex.getMessage().contains("Requester"));
    }

    @Test
    @DisplayName("Falla el comando si el destinatario es nulo")
    void shouldFailCommand_WhenTargetNull() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new CreateDirectConversationCommand(requester, null, companyId));
        assertTrue(ex.getMessage().contains("Target"));
    }

    @Test
    @DisplayName("Falla el comando si se intenta conversar consigo mismo")
    void shouldFailCommand_WhenRequesterEqualsTarget() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new CreateDirectConversationCommand(requester, requester, companyId));
        assertTrue(ex.getMessage().contains("yourself"));
    }

    @Test
    @DisplayName("Falla el comando si falta el companyId")
    void shouldFailCommand_WhenCompanyIdNull() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new CreateDirectConversationCommand(requester, target, null));
        assertTrue(ex.getMessage().contains("Company ID"));
    }

    // --- Group.createDirectConversation ---

    @Test
    @DisplayName("La conversación directa es tipo DIRECT, privada y con exactamente 2 miembros")
    void shouldBuildDirectConversation_WithTwoMembersAndDirectType() {
        Group conversation = Group.createDirectConversation(requester, target, companyId);

        assertTrue(conversation.isDirect());
        assertEquals(GroupType.DIRECT, conversation.getType());
        assertEquals(GroupVisibility.PRIVATE, conversation.getVisibility());
        assertEquals(2, conversation.getMembers().size());
        assertTrue(conversation.isMember(requester));
        assertTrue(conversation.isMember(target));
        assertEquals(requester, conversation.getCreatedBy());
        assertEquals(companyId, conversation.getCompanyId());
    }

    @Test
    @DisplayName("Falla crear conversación directa consigo mismo")
    void shouldFailDirectConversation_WhenSameUser() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> Group.createDirectConversation(requester, requester, companyId));
        assertTrue(ex.getMessage().contains("yourself"));
    }

    @Test
    @DisplayName("Falla crear conversación directa con participante nulo")
    void shouldFailDirectConversation_WhenParticipantNull() {
        assertThrows(IllegalArgumentException.class,
                () -> Group.createDirectConversation(requester, null, companyId));
    }

    @Test
    @DisplayName("Un grupo normal no es una conversación directa (tipo GROUP)")
    void shouldNotBeDirect_WhenRegularGroup() {
        Group group = new Group("Equipo", "desc", null,
                GroupVisibility.PRIVATE, List.of(target.userId()), requester);

        assertFalse(group.isDirect());
        assertEquals(GroupType.GROUP, group.getType());
    }

    // --- ConversationResourceFromEntityAssembler ---

    @Test
    @DisplayName("El recurso resuelve 'el otro participante' respecto al usuario actual")
    void shouldResolveOtherParticipant() {
        Group conversation = Group.createDirectConversation(requester, target, companyId);

        ConversationResource fromRequester =
                ConversationResourceFromEntityAssembler.toResourceFromEntity(conversation, requester.userId());
        ConversationResource fromTarget =
                ConversationResourceFromEntityAssembler.toResourceFromEntity(conversation, target.userId());

        assertEquals(target.userId(), fromRequester.otherUserId());
        assertEquals(requester.userId(), fromTarget.otherUserId());
        assertEquals(2, fromRequester.memberIds().size());
        assertTrue(fromRequester.memberIds().contains(requester.userId()));
        assertTrue(fromRequester.memberIds().contains(target.userId()));
    }

    // --- GroupResourceFromEntityAssembler (campo type) ---

    @Test
    @DisplayName("GroupResource expone el tipo DIRECT para una conversación directa")
    void shouldExposeDirectType_InGroupResource() {
        Group conversation = Group.createDirectConversation(requester, target, companyId);

        GroupResource resource = GroupResourceFromEntityAssembler.toResourceFromEntity(conversation);

        assertEquals("DIRECT", resource.type());
    }

    @Test
    @DisplayName("GroupResource expone el tipo GROUP para un grupo normal")
    void shouldExposeGroupType_InGroupResource() {
        Group group = new Group("Equipo", "desc", null,
                GroupVisibility.PRIVATE, List.of(target.userId()), requester);

        GroupResource resource = GroupResourceFromEntityAssembler.toResourceFromEntity(group);

        assertEquals("GROUP", resource.type());
    }
}
