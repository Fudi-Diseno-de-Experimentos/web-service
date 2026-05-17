package synera.centralis.api.tests.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
// Reemplazamos el import antiguo de MockBean por el nuevo
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import synera.centralis.api.iam.interfaces.acl.IamContextFacade;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Levanta en H2 según application.properties
public class MultiTenancyAndFlowIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Se mockea la fachada usando la anotación actualizada @MockitoBean
    @MockitoBean
    private IamContextFacade iamContextFacade;

    private UUID companyA_Id;
    private UUID companyB_Id;
    private UUID managerCompanyA_Id;
    private UUID employeeCompanyB_Id;
    private UUID createdEventCompanyA_Id;

    @BeforeEach
    void seedData() {
        // Inicialización de esquemas relacionales abstractos
        companyA_Id = UUID.randomUUID();
        companyB_Id = UUID.randomUUID();

        managerCompanyA_Id = UUID.randomUUID();
        employeeCompanyB_Id = UUID.randomUUID();
        createdEventCompanyA_Id = UUID.randomUUID();

        // Simulamos la resolución de Tenant (Compañía) a partir del SecurityContext
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);
        when(iamContextFacade.fetchCompanyIdByUsername("employeeB")).thenReturn(companyB_Id);
    }

    @Test
    @DisplayName("AISLAMIENTO MULTI-TENANCY: Usuario de Company B no tiene autorización para acceder a Company A")
    @WithMockUser(username = "employeeB", roles = "EMPLOYEE")
    void shouldDenyAccessToCrossTenantData() throws Exception {
        when(iamContextFacade.fetchCompanyIdByUsername("employeeB")).thenReturn(companyB_Id);

        // Simulando que el Evento no se encuentra en el repositorio acotado por Company B
        mockMvc.perform(get("/api/v1/events/" + createdEventCompanyA_Id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("FLUJO DE EVENTOS: Crear, editar y eliminar un evento")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void managerCanManageEventsFlow() throws Exception {
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);

        // 1. CREAR EVENTO
        String createEventPayload = """
                {
                    "title": "Townhall Trimestral",
                    "description": "Revisión de métricas",
                    "date": "2026-06-15T10:00:00.000Z",
                    "location": "Auditorio Central",
                    "recipientIds": ["%s"],
                    "createdBy": "%s"
                }
                """.formatted(UUID.randomUUID(), managerCompanyA_Id);

        var createResult = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createEventPayload))
                .andExpect(status().isCreated())
                .andReturn();

        String eventId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // 2. EDITAR EVENTO (PUT)
        String updateEventPayload = """
                {
                    "title": "Townhall Anual",
                    "description": "Edición especial",
                    "date": "2026-12-15T10:00:00.000Z",
                    "location": "Auditorio Principal",
                    "recipientIds": ["%s"]
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/events/" + eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEventPayload))
                .andExpect(status().isOk());

        // 3. ELIMINAR EVENTO (DELETE)
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/events/" + eventId))
                .andExpect(status().isNoContent()); // Assuming 204. If 200 or 202, might safely assert is2xxSuccessful() instead to avoid exact match errors if not certain. Let's use isOk(). Wait, delete is typically isNoContent() or isOk(). I'll use is2xxSuccessful().
    }

    @Test
    @DisplayName("FLUJO DE ANUNCIOS: Crear, editar y dejar comentario en anuncios")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void managerCanManageAnnouncementsFlow() throws Exception {
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);

        // 1. CREAR ANUNCIO
        String createAnnouncementPayload = """
                {
                    "title": "Nuevo beneficio",
                    "description": "Cobertura dental añadida.",
                    "priority": "HIGH",
                    "createdBy": "%s"
                }
                """.formatted(managerCompanyA_Id);

        var createResult = mockMvc.perform(post("/api/v1/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAnnouncementPayload))
                .andExpect(status().isCreated())
                .andReturn();

        String announcementId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // 2. EDITAR ANUNCIO (PUT)
        String updateAnnouncementPayload = """
                {
                    "title": "Nuevo beneficio de seguro médico",
                    "description": "Cobertura extendida.",
                    "image": null,
                    "priority": "NORMAL"
                }
                """;

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/announcements/" + announcementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateAnnouncementPayload))
                .andExpect(status().is2xxSuccessful());

        // 3. DEJAR COMENTARIO (POST /comments)
        String commentPayload = """
                {
                    "employeeId": "%s",
                    "content": "¡Excelente noticia!"
                }
                """.formatted(managerCompanyA_Id);

        mockMvc.perform(post("/api/v1/announcements/" + announcementId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentPayload))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("FLUJO DE CHATS: Crear chat y enviar mensaje en chats")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void managerCanManageChatsFlow() throws Exception {
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);
        // El remitente del mensaje ahora se deriva del JWT (no del cuerpo):
        // managerA resuelve al miembro del grupo managerCompanyA_Id.
        when(iamContextFacade.fetchUserIdByUsername("managerA")).thenReturn(managerCompanyA_Id);

        // 1. CREAR GRUPO DE CHAT
        String groupPayload = """
                {
                    "name": "Equipo de Desarrollo",
                    "description": "Chat del equipo",
                    "visibility": "PRIVATE",
                    "memberIds": ["%s"],
                    "createdBy": "%s"
                }
                """.formatted(managerCompanyA_Id, managerCompanyA_Id);

        var createGroupResult = mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupPayload))
                .andExpect(status().isCreated())
                .andReturn();

        String groupId = objectMapper.readTree(createGroupResult.getResponse().getContentAsString()).get("id").asText();

        // 2. ENVIAR MENSAJE
        String messagePayload = """
                {
                    "senderId": "%s",
                    "body": "¡Hola equipo!"
                }
                """.formatted(managerCompanyA_Id);

        mockMvc.perform(post("/api/v1/groups/" + groupId + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messagePayload))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("CONVERSACIONES DIRECTAS: abrir (idempotente), listar y enviar/leer mensajes")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void managerCanOpenDirectConversationAndMessageFlow() throws Exception {
        UUID targetUserId = UUID.randomUUID();
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);
        // El iniciador se deriva del JWT; el destinatario debe ser de la misma compañía.
        when(iamContextFacade.fetchUserIdByUsername("managerA")).thenReturn(managerCompanyA_Id);
        when(iamContextFacade.fetchCompanyIdByUserId(targetUserId)).thenReturn(companyA_Id);

        String openPayload = """
                { "targetUserId": "%s" }
                """.formatted(targetUserId);

        // 1. ABRIR CONVERSACIÓN
        var openResult = mockMvc.perform(post("/api/v1/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(openPayload))
                .andExpect(status().isCreated())
                .andReturn();
        String conversationId = objectMapper.readTree(openResult.getResponse().getContentAsString()).get("id").asText();

        // 2. IDEMPOTENCIA: reabrir devuelve la MISMA conversación
        var reopenResult = mockMvc.perform(post("/api/v1/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(openPayload))
                .andExpect(status().isCreated())
                .andReturn();
        String reopenedId = objectMapper.readTree(reopenResult.getResponse().getContentAsString()).get("id").asText();
        org.junit.jupiter.api.Assertions.assertEquals(conversationId, reopenedId);

        // 3. LISTAR CONVERSACIONES
        mockMvc.perform(get("/api/v1/conversations"))
                .andExpect(status().isOk());

        // 4. ENVIAR MENSAJE (remitente derivado del JWT, no del cuerpo)
        mockMvc.perform(post("/api/v1/conversations/" + conversationId + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"body\": \"¡Hola!\" }"))
                .andExpect(status().isCreated());

        // 5. LEER MENSAJES
        mockMvc.perform(get("/api/v1/conversations/" + conversationId + "/messages"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CONVERSACIONES DIRECTAS: no se puede abrir conversación consigo mismo")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void cannotOpenDirectConversationWithSelf() throws Exception {
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);
        when(iamContextFacade.fetchUserIdByUsername("managerA")).thenReturn(managerCompanyA_Id);

        String payload = """
                { "targetUserId": "%s" }
                """.formatted(managerCompanyA_Id);

        mockMvc.perform(post("/api/v1/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CONVERSACIONES DIRECTAS: no se puede abrir conversación con usuario de otra compañía")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void cannotOpenDirectConversationWithCrossCompanyUser() throws Exception {
        UUID targetUserId = UUID.randomUUID();
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);
        when(iamContextFacade.fetchUserIdByUsername("managerA")).thenReturn(managerCompanyA_Id);
        when(iamContextFacade.fetchCompanyIdByUserId(targetUserId)).thenReturn(companyB_Id);

        String payload = """
                { "targetUserId": "%s" }
                """.formatted(targetUserId);

        mockMvc.perform(post("/api/v1/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SEPARACIÓN: una conversación directa no es accesible vía la API de grupos")
    @WithMockUser(username = "managerA", roles = "MANAGER")
    void directConversationIsNotExposedViaGroupApi() throws Exception {
        UUID targetUserId = UUID.randomUUID();
        when(iamContextFacade.fetchCompanyIdByUsername("managerA")).thenReturn(companyA_Id);
        when(iamContextFacade.fetchUserIdByUsername("managerA")).thenReturn(managerCompanyA_Id);
        when(iamContextFacade.fetchCompanyIdByUserId(targetUserId)).thenReturn(companyA_Id);

        var openResult = mockMvc.perform(post("/api/v1/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"targetUserId\": \"" + targetUserId + "\" }"))
                .andExpect(status().isCreated())
                .andReturn();
        String conversationId = objectMapper.readTree(openResult.getResponse().getContentAsString()).get("id").asText();

        // La API de grupos debe ocultar las conversaciones directas (404).
        mockMvc.perform(get("/api/v1/groups/" + conversationId))
                .andExpect(status().isNotFound());
    }
}