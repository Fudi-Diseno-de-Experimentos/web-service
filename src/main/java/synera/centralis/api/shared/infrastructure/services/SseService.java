package synera.centralis.api.shared.infrastructure.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Service for managing Server-Sent Events (SSE) connections
 * Handles real-time communication between server and clients
 */
@Service
public class SseService {
    
    private static final Logger logger = Logger.getLogger(SseService.class.getName());
    
    // Mapa de conexiones activas: groupId -> clientId -> SseEmitter
    private final Map<String, Map<String, SseEmitter>> groupEmitters = new ConcurrentHashMap<>();
    
    // Timeout por defecto: 30 minutos (para conexiones largas)
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutos
    
    /**
     * Agrega un cliente SSE a un grupo de chat
     * 
     * @param groupId ID del grupo de chat
     * @param clientId ID único del cliente (userId + timestamp)
     * @return SseEmitter para la conexión
     */
    public SseEmitter addClient(String groupId, String clientId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        
        // Agregar emitter al grupo
        groupEmitters.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>()).put(clientId, emitter);
        
        logger.info("🔗 SSE: Cliente conectado - Grupo: " + groupId + ", Cliente: " + clientId);
        logger.info("📊 SSE: Total conexiones en grupo " + groupId + ": " + getActiveConnections(groupId));
        
        // Configurar callbacks para limpieza automática
        emitter.onCompletion(() -> {
            removeClient(groupId, clientId);
            logger.info("✅ SSE: Cliente desconectado (completado) - " + clientId);
        });
        
        emitter.onTimeout(() -> {
            removeClient(groupId, clientId);
            logger.info("⏰ SSE: Cliente desconectado (timeout) - " + clientId);
        });
        
        emitter.onError((throwable) -> {
            removeClient(groupId, clientId);
            logger.warning("❌ SSE: Cliente desconectado (error) - " + clientId + ": " + throwable.getMessage());
        });
        
        // Enviar evento de bienvenida
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Conectado al grupo: " + groupId));
        } catch (IOException e) {
            logger.warning("Error enviando evento de bienvenida: " + e.getMessage());
            emitter.complete();
        }
        
        return emitter;
    }
    
    /**
     * Envía un mensaje a todos los clientes conectados de un grupo
     * 
     * @param groupId ID del grupo
     * @param eventType Tipo de evento (message_created, user_typing, etc.)
     * @param data Datos del evento
     */
    public void sendToGroup(String groupId, String eventType, Object data) {
        Map<String, SseEmitter> groupClients = groupEmitters.get(groupId);
        
        if (groupClients == null || groupClients.isEmpty()) {
            logger.info("📭 SSE: No hay clientes conectados en grupo: " + groupId);
            return;
        }
        
        logger.info("📡 SSE: Enviando evento '" + eventType + "' a " + groupClients.size() + " clientes del grupo: " + groupId);
        
        // Enviar evento a todos los clientes del grupo
        groupClients.entrySet().removeIf(entry -> {
            String clientId = entry.getKey();
            SseEmitter emitter = entry.getValue();
            
            try {
                emitter.send(SseEmitter.event()
                        .name(eventType)
                        .data(data));
                return false; // Mantener cliente
                
            } catch (IOException e) {
                logger.warning("Error enviando a cliente " + clientId + ": " + e.getMessage());
                emitter.complete();
                return true; // Remover cliente
            }
        });
        
        logger.info("✅ SSE: Evento enviado exitosamente a grupo: " + groupId);
    }
    
    /**
     * Envía un evento a un cliente específico
     * 
     * @param groupId ID del grupo
     * @param clientId ID del cliente
     * @param eventType Tipo de evento
     * @param data Datos del evento
     */
    public void sendToClient(String groupId, String clientId, String eventType, Object data) {
        Map<String, SseEmitter> groupClients = groupEmitters.get(groupId);
        
        if (groupClients == null) {
            logger.warning("Grupo no encontrado: " + groupId);
            return;
        }
        
        SseEmitter emitter = groupClients.get(clientId);
        if (emitter == null) {
            logger.warning("Cliente no encontrado: " + clientId + " en grupo: " + groupId);
            return;
        }
        
        try {
            emitter.send(SseEmitter.event()
                    .name(eventType)
                    .data(data));
            
            logger.info("📤 SSE: Evento enviado a cliente específico: " + clientId);
            
        } catch (IOException e) {
            logger.warning("Error enviando a cliente específico " + clientId + ": " + e.getMessage());
            groupClients.remove(clientId);
            emitter.complete();
        }
    }
    
    /**
     * Remueve un cliente de un grupo
     * 
     * @param groupId ID del grupo
     * @param clientId ID del cliente
     */
    private void removeClient(String groupId, String clientId) {
        Map<String, SseEmitter> groupClients = groupEmitters.get(groupId);
        
        if (groupClients != null) {
            groupClients.remove(clientId);
            
            // Si no quedan clientes en el grupo, remover el grupo completo
            if (groupClients.isEmpty()) {
                groupEmitters.remove(groupId);
                logger.info("🗑️ SSE: Grupo removido (sin clientes): " + groupId);
            }
        }
    }
    
    /**
     * Obtiene el número de conexiones activas en un grupo
     * 
     * @param groupId ID del grupo
     * @return Número de conexiones activas
     */
    public int getActiveConnections(String groupId) {
        Map<String, SseEmitter> groupClients = groupEmitters.get(groupId);
        return groupClients != null ? groupClients.size() : 0;
    }
    
    /**
     * Obtiene el número total de conexiones activas en todos los grupos
     * 
     * @return Número total de conexiones
     */
    public int getTotalActiveConnections() {
        return groupEmitters.values().stream()
                .mapToInt(Map::size)
                .sum();
    }
    
    /**
     * Obtiene estadísticas de conexiones SSE
     * 
     * @return Mapa con estadísticas
     */
    public Map<String, Object> getConnectionStats() {
        return Map.of(
                "totalGroups", groupEmitters.size(),
                "totalConnections", getTotalActiveConnections(),
                "groupStats", groupEmitters.entrySet().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().size()
                        ))
        );
    }
    
    /**
     * Cierra todas las conexiones de un grupo
     * 
     * @param groupId ID del grupo
     */
    public void closeGroupConnections(String groupId) {
        Map<String, SseEmitter> groupClients = groupEmitters.get(groupId);
        
        if (groupClients != null) {
            groupClients.values().forEach(SseEmitter::complete);
            groupEmitters.remove(groupId);
            logger.info("🔒 SSE: Cerradas todas las conexiones del grupo: " + groupId);
        }
    }
}