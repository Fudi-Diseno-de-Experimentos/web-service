package synera.centralis.api.chat.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket + STOMP Configuration.
 * <p>
 * Registra el endpoint de WebSocket en /ws-chat con soporte SockJS como fallback.
 * Configura el broker de mensajes en memoria:
 * - /topic  → broadcast a múltiples suscriptores (mensajes de grupo)
 * - /app    → prefijo para mensajes dirigidos al servidor
 * </p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthChannelInterceptor authChannelInterceptor;

    public WebSocketConfiguration(WebSocketAuthChannelInterceptor authChannelInterceptor) {
        this.authChannelInterceptor = authChannelInterceptor;
    }

    /**
     * Registra el endpoint WebSocket accesible en ws://host/ws-chat.
     * SockJS permite fallback para entornos que no soporten WebSocket nativo.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * Configura el broker de mensajes:
     * - /topic : destino de broadcast (el servidor envía a todos los suscriptores)
     * - /app   : prefijo para los destinos que maneja @MessageMapping
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra el interceptor JWT en el canal de entrada de mensajes.
     * Valida el token en el frame CONNECT de STOMP.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
    }
}
