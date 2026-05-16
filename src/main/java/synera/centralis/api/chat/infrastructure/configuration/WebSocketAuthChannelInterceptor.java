package synera.centralis.api.chat.infrastructure.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import synera.centralis.api.iam.infrastructure.tokens.jwt.BearerTokenService;

/**
 * WebSocket JWT Authentication Channel Interceptor.
 * <p>
 * Intercepta el frame STOMP CONNECT y valida el token JWT enviado en el header
 * "Authorization: Bearer &lt;token&gt;".
 * Reutiliza el {@link BearerTokenService} y {@link UserDetailsService} existentes del módulo IAM.
 * </p>
 *
 * <p>Si el token es válido, establece la autenticación en el contexto del WebSocket
 * para que {@link org.springframework.messaging.handler.annotation.MessageMapping}
 * pueda acceder al {@link java.security.Principal} autenticado.</p>
 *
 * <p>Si el token es inválido o está ausente, la conexión STOMP se rechaza lanzando
 * una excepción.</p>
 */
@Slf4j
@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final BearerTokenService tokenService;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthChannelInterceptor(
            BearerTokenService tokenService,
            @Qualifier("defaultUserDetailsService") UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Se ejecuta antes de enviar el mensaje al canal.
     * En el frame CONNECT de STOMP, extrae y valida el JWT.
     *
     * @param message El mensaje STOMP entrante
     * @param channel El canal de mensajes
     * @return El mensaje (con autenticación establecida si el token es válido)
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.info("[WebSocket] Frame CONNECT recibido — header Authorization: {}",
                    authHeader != null ? "presente" : "ausente");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);

                try {
                    if (tokenService.validateToken(jwt)) {
                        String username = tokenService.getUsernameFromToken(jwt);
                        log.info("[WebSocket] Token válido — usuario autenticado: {}", username);

                        var userDetails = userDetailsService.loadUserByUsername(username);
                        var authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        accessor.setUser(authentication);

                        log.info("[WebSocket] Autenticación establecida para usuario: {}", username);
                    } else {
                        log.warn("[WebSocket] Token JWT inválido — conexión rechazada");
                        throw new IllegalArgumentException("Token JWT inválido");
                    }
                } catch (Exception e) {
                    log.error("[WebSocket] Error al validar el token: {}", e.getMessage());
                    throw new IllegalArgumentException("Error de autenticación WebSocket: " + e.getMessage());
                }
            } else {
                log.warn("[WebSocket] Header Authorization ausente o mal formado — conexión rechazada");
                throw new IllegalArgumentException("Se requiere autenticación JWT para conectar al WebSocket");
            }
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            String username = (accessor.getUser() != null) ? accessor.getUser().getName() : "desconocido";
            log.info("[WebSocket] Usuario desconectado: {}", username);
        }

        return message;
    }
}
