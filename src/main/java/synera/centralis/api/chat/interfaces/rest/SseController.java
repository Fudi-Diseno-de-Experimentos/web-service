package synera.centralis.api.chat.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import synera.centralis.api.shared.infrastructure.services.SseService;

import java.util.Map;
import java.util.UUID;

/**
 * Controller for Server-Sent Events (SSE) endpoints
 * Manages real-time connections for chat functionality
 */
@RestController
@RequestMapping("/api/v1/sse")
@Tag(name = "SSE - Real-time Chat", description = "Server-Sent Events endpoints for real-time chat communication")
public class SseController {
    
    private final SseService sseService;
    
    public SseController(SseService sseService) {
        this.sseService = sseService;
    }
    
    /**
     * Establece una conexión SSE para recibir mensajes en tiempo real de un grupo de chat
     * 
     * @param groupId ID del grupo de chat
     * @param userId ID del usuario que se conecta
     * @return SseEmitter para la conexión en tiempo real
     */
    @GetMapping(value = "/chat/{groupId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "Conectar a chat en tiempo real",
            description = "Establece una conexión Server-Sent Events para recibir mensajes en tiempo real de un grupo específico. " +
                         "La conexión permanece abierta y envía eventos cuando se crean nuevos mensajes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conexión SSE establecida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado"),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado")
    })
    public SseEmitter connectToChat(
            @Parameter(description = "ID del grupo de chat", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String groupId,
            
            @Parameter(description = "ID del usuario que se conecta", required = true, example = "622a0852-d80c-4da8-b66f-101a8df4aaa1")
            @RequestParam String userId) {
        
        // Generar ID único para esta conexión
        String clientId = userId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        
        // Crear y retornar la conexión SSE
        return sseService.addClient(groupId, clientId);
    }
    
    /**
     * Obtiene estadísticas de conexiones SSE activas
     * 
     * @return Estadísticas de conexiones
     */
    @GetMapping("/stats")
    @Operation(
            summary = "Obtener estadísticas SSE",
            description = "Devuelve información sobre las conexiones Server-Sent Events activas, " +
                         "incluyendo número de grupos, conexiones totales y estadísticas por grupo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado")
    })
    public ResponseEntity<Map<String, Object>> getConnectionStats() {
        Map<String, Object> stats = sseService.getConnectionStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Obtiene el número de conexiones activas para un grupo específico
     * 
     * @param groupId ID del grupo
     * @return Número de conexiones activas
     */
    @GetMapping("/chat/{groupId}/connections")
    @Operation(
            summary = "Conexiones activas del grupo",
            description = "Devuelve el número de usuarios conectados en tiempo real a un grupo específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de conexiones obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado")
    })
    public ResponseEntity<Map<String, Object>> getGroupConnections(
            @Parameter(description = "ID del grupo de chat", required = true)
            @PathVariable String groupId) {
        
        int activeConnections = sseService.getActiveConnections(groupId);
        
        Map<String, Object> response = Map.of(
                "groupId", groupId,
                "activeConnections", activeConnections,
                "isActive", activeConnections > 0
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cierra todas las conexiones de un grupo (endpoint administrativo)
     * 
     * @param groupId ID del grupo
     * @return Respuesta de confirmación
     */
    @PostMapping("/chat/{groupId}/close")
    @Operation(
            summary = "Cerrar conexiones del grupo",
            description = "Cierra todas las conexiones SSE activas de un grupo específico. " +
                         "Endpoint administrativo para gestión de conexiones."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conexiones cerradas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado")
    })
    public ResponseEntity<Map<String, String>> closeGroupConnections(
            @Parameter(description = "ID del grupo de chat", required = true)
            @PathVariable String groupId) {
        
        sseService.closeGroupConnections(groupId);
        
        Map<String, String> response = Map.of(
                "message", "Conexiones cerradas para el grupo: " + groupId,
                "groupId", groupId,
                "status", "success"
        );
        
        return ResponseEntity.ok(response);
    }
}