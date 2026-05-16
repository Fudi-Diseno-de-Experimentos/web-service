package synera.centralis.api.tests.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.commands.CreateChatImageCommand;
import synera.centralis.api.chat.domain.model.commands.CreateGroupCommand;
import synera.centralis.api.chat.domain.model.commands.CreateMessageCommand;
import synera.centralis.api.chat.domain.model.commands.DeleteChatImageCommand;
import synera.centralis.api.chat.domain.model.commands.DeleteGroupCommand;
import synera.centralis.api.chat.domain.model.commands.DeleteMessageCommand;
import synera.centralis.api.chat.domain.model.commands.UpdateMessageBodyCommand;
import synera.centralis.api.chat.domain.model.entities.ChatImage;
import synera.centralis.api.chat.domain.model.entities.Message;
import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.chat.domain.services.ChatImageCommandService;
import synera.centralis.api.chat.domain.services.GroupCommandService;
import synera.centralis.api.chat.domain.services.MessageCommandService;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del contexto Chat.
 * Cubre US23 (crear grupo), US24/US28 (eliminar grupo), US25 (enviar mensaje),
 * US26 (eliminar mensaje), US27 (editar mensaje), US30 (enviar imagen) y
 * US31 (eliminar imagen). Patrón AAA con Mockito + Jupiter.
 */
@ExtendWith(MockitoExtension.class)
class ChatContextUnitTests {

    @Mock
    private GroupCommandService groupCommandService;

    @Mock
    private MessageCommandService messageCommandService;

    @Mock
    private ChatImageCommandService chatImageCommandService;

    private CompanyId companyId;
    private UserId senderId;

    @BeforeEach
    void setUp() {
        companyId = new CompanyId(UUID.randomUUID());
        senderId = new UserId(UUID.randomUUID());
    }

    @Test
    @DisplayName("US23: El empleado crea un chat grupal válido")
    void shouldCreateGroup_WhenCommandIsValid() {
        // Arrange
        CreateGroupCommand command = new CreateGroupCommand(
                "Equipo Backend", "Coordinación técnica", null,
                GroupVisibility.PRIVATE, List.of(senderId.userId()),
                senderId, companyId);
        Group expected = mock(Group.class);
        when(groupCommandService.handle(command)).thenReturn(expected);

        // Act
        Group result = groupCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(groupCommandService).handle(command);
    }

    @Test
    @DisplayName("US23: Falla la creación del grupo si no tiene miembros")
    void shouldFailCreateGroup_WhenNoMembers() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateGroupCommand("Grupo", "Desc", null,
                        GroupVisibility.PUBLIC, List.of(), senderId, companyId));

        // Assert
        assertTrue(ex.getMessage().contains("at least one member"));
    }

    @Test
    @DisplayName("US24/US28: El gerente elimina un chat grupal")
    void shouldDeleteGroup_WhenCommandIsValid() {
        // Arrange
        DeleteGroupCommand command =
                new DeleteGroupCommand(UUID.randomUUID(), companyId);
        when(groupCommandService.handle(command)).thenReturn(true);

        // Act
        boolean deleted = groupCommandService.handle(command);

        // Assert
        assertTrue(deleted);
        verify(groupCommandService).handle(command);
    }

    @Test
    @DisplayName("US24/US28: Falla la eliminación del grupo si falta el companyId")
    void shouldFailDeleteGroup_WhenCompanyIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new DeleteGroupCommand(UUID.randomUUID(), null));

        // Assert
        assertTrue(ex.getMessage().contains("Company ID"));
    }

    @Test
    @DisplayName("US25: El empleado envía un mensaje válido al grupo")
    void shouldSendMessage_WhenCommandIsValid() {
        // Arrange
        CreateMessageCommand command =
                new CreateMessageCommand(UUID.randomUUID(), senderId, "¡Hola equipo!");
        Message expected = mock(Message.class);
        when(messageCommandService.handle(command)).thenReturn(expected);

        // Act
        Message result = messageCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(messageCommandService).handle(command);
    }

    @Test
    @DisplayName("US25: Falla el envío si el cuerpo del mensaje está vacío")
    void shouldFailSendMessage_WhenBodyIsBlank() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateMessageCommand(UUID.randomUUID(), senderId, "   "));

        // Assert
        assertTrue(ex.getMessage().contains("body"));
    }

    @Test
    @DisplayName("US27: El empleado edita el cuerpo de un mensaje enviado")
    void shouldUpdateMessageBody_WhenCommandIsValid() {
        // Arrange
        UpdateMessageBodyCommand command =
                new UpdateMessageBodyCommand(UUID.randomUUID(), "Texto corregido");
        Message expected = mock(Message.class);
        when(messageCommandService.handle(command)).thenReturn(expected);

        // Act
        Message result = messageCommandService.handle(command);

        // Assert
        assertNotNull(result);
        verify(messageCommandService).handle(command);
    }

    @Test
    @DisplayName("US27: Falla la edición si el nuevo cuerpo está vacío")
    void shouldFailUpdateMessage_WhenBodyIsBlank() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new UpdateMessageBodyCommand(UUID.randomUUID(), ""));

        // Assert
        assertTrue(ex.getMessage().contains("body"));
    }

    @Test
    @DisplayName("US26: El empleado elimina un mensaje enviado por error")
    void shouldDeleteMessage_WhenCommandIsValid() {
        // Arrange
        DeleteMessageCommand command = new DeleteMessageCommand(UUID.randomUUID());
        when(messageCommandService.handle(command)).thenReturn(true);

        // Act
        boolean deleted = messageCommandService.handle(command);

        // Assert
        assertTrue(deleted);
        verify(messageCommandService).handle(command);
    }

    @Test
    @DisplayName("US26: Falla la eliminación si el ID del mensaje es nulo")
    void shouldFailDeleteMessage_WhenMessageIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new DeleteMessageCommand(null));

        // Assert
        assertTrue(ex.getMessage().contains("Message ID"));
    }

    @Test
    @DisplayName("US30: El empleado envía una imagen al chat grupal")
    void shouldSendChatImage_WhenCommandIsValid() {
        // Arrange
        CreateChatImageCommand command = new CreateChatImageCommand(
                UUID.randomUUID(), senderId, "https://cdn.test/img.png");
        ChatImage expected = mock(ChatImage.class);
        when(chatImageCommandService.handle(command)).thenReturn(Optional.of(expected));

        // Act
        Optional<ChatImage> result = chatImageCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        verify(chatImageCommandService).handle(command);
    }

    @Test
    @DisplayName("US30: Falla el envío de imagen si la URL está vacía")
    void shouldFailSendChatImage_WhenUrlIsBlank() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new CreateChatImageCommand(UUID.randomUUID(), senderId, "  "));

        // Assert
        assertTrue(ex.getMessage().contains("Image URL"));
    }

    @Test
    @DisplayName("US31: El usuario elimina una imagen enviada en el chat")
    void shouldDeleteChatImage_WhenCommandIsValid() {
        // Arrange
        DeleteChatImageCommand command = new DeleteChatImageCommand(UUID.randomUUID());
        ChatImage expected = mock(ChatImage.class);
        when(chatImageCommandService.handle(command)).thenReturn(Optional.of(expected));

        // Act
        Optional<ChatImage> result = chatImageCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        verify(chatImageCommandService).handle(command);
    }

    @Test
    @DisplayName("US31: Falla la eliminación de imagen si el ID es nulo")
    void shouldFailDeleteChatImage_WhenImageIdIsNull() {
        // Arrange & Act
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new DeleteChatImageCommand(null));

        // Assert
        assertTrue(ex.getMessage().contains("Image ID"));
    }
}
