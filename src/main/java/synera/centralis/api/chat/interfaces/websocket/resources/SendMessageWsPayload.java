package synera.centralis.api.chat.interfaces.websocket.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Payload de entrada para enviar un mensaje a través de WebSocket/STOMP.
 * <p>
 * El cliente Flutter (stomp_dart_client) envía este JSON al destino
 * {@code /app/chat.send/{groupId}}.
 * </p>
 *
 * <p>Ejemplo de cuerpo JSON que envía el cliente:</p>
 * <pre>
 * {
 *   "senderId": "123e4567-e89b-12d3-a456-426614174000",
 *   "body": "Hola equipo!"
 * }
 * </pre>
 */
public record SendMessageWsPayload(

        @NotNull(message = "El ID del remitente es obligatorio")
        UUID senderId,

        @NotBlank(message = "El cuerpo del mensaje es obligatorio")
        @Size(max = 1000, message = "El mensaje no puede superar los 1000 caracteres")
        String body
) {
}
