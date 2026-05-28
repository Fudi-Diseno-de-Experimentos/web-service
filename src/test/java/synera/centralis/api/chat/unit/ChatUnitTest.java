package synera.centralis.api.chat.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import synera.centralis.api.chat.domain.model.aggregates.Group;
import synera.centralis.api.chat.domain.model.valueobjects.GroupVisibility;
import synera.centralis.api.chat.domain.model.valueobjects.UserId;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios del contexto Chat (US23-US28).
 * Se prueban las invariantes y el comportamiento del agregado Group, incluida
 * la conversación directa.
 */
@DisplayName("Chat - Reglas de negocio de grupos (US23-US28)")
class ChatUnitTest {

    private static final UserId CREADOR = new UserId(UUID.randomUUID());

    private Group grupoConUnMiembroExtra() {
        return new Group("Proyecto Alpha", "Coordinación", null, GroupVisibility.PUBLIC,
                List.of(UUID.randomUUID()), CREADOR);
    }

    @Test
    @DisplayName("Happy: crear un grupo agrega automáticamente al creador como miembro")
    void crearGrupoAgregaCreador() {
        // Business / User Story Rational (US23): al crear el chat con participantes,
        // el sistema crea el grupo con todos los miembros añadidos (incluido el creador).
        // Act
        var grupo = grupoConUnMiembroExtra();
        // Assert
        assertTrue(grupo.isMember(CREADOR));
        assertEquals(2, grupo.getMemberCount());
        assertEquals(GroupVisibility.PUBLIC, grupo.getVisibility());
    }

    @Test
    @DisplayName("Límite superior: nombre de grupo de 101 caracteres es rechazado")
    void nombreSobreLimiteEsRechazado() {
        // Business / User Story Rational (US23): el nombre del grupo admite hasta
        // 100 caracteres.
        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Group("N".repeat(101), null, null, GroupVisibility.PUBLIC, List.of(), CREADOR));
    }

    @Test
    @DisplayName("Límite inferior: no se puede quitar al último miembro del grupo")
    void noQuitarUltimoMiembro() {
        // Business / User Story Rational (US24/US28): un grupo no puede quedar
        // vacío; eliminar el último miembro es un estado inválido.
        // Arrange: grupo solo con el creador
        var grupo = new Group("Solo", null, null, GroupVisibility.PRIVATE, List.of(), CREADOR);
        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class, () -> grupo.removeMember(CREADOR));
        assertEquals("Cannot remove the last member from the group", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción por estado inválido: no se puede agregar un miembro duplicado")
    void noAgregarMiembroDuplicado() {
        // Business / User Story Rational (US23): añadir dos veces al mismo
        // participante rompe la integridad de la membresía.
        // Arrange
        var grupo = grupoConUnMiembroExtra();
        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class, () -> grupo.addMember(CREADOR));
        assertEquals("User is already a member of this group", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción por estado inválido: no se puede quitar a un no miembro")
    void noQuitarNoMiembro() {
        // Business / User Story Rational (US24): moderar miembros exige que el
        // usuario realmente pertenezca al grupo.
        // Arrange
        var grupo = grupoConUnMiembroExtra();
        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> grupo.removeMember(new UserId(UUID.randomUUID())));
    }

    @Test
    @DisplayName("Datos insuficientes: una conversación directa con uno mismo es inválida")
    void conversacionDirectaConsigoMismoEsInvalida() {
        // Business / User Story Rational (US25): un chat directo requiere dos
        // participantes distintos.
        // Arrange
        var compania = new CompanyId(UUID.randomUUID());
        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class,
                () -> Group.createDirectConversation(CREADOR, CREADOR, compania));
        assertEquals("Cannot start a direct conversation with yourself", ex.getMessage());
    }

    @Test
    @DisplayName("Condicional A: una conversación directa se marca como directa, un grupo no")
    void conversacionDirectaSeMarcaComoDirecta() {
        // Business / User Story Rational (US25): las conversaciones 1 a 1 se
        // distinguen de los grupos para enrutarse a su propio recurso.
        // Arrange
        var otro = new UserId(UUID.randomUUID());
        var compania = new CompanyId(UUID.randomUUID());
        // Act
        var directa = Group.createDirectConversation(CREADOR, otro, compania);
        var grupo = grupoConUnMiembroExtra();
        // Assert
        assertTrue(directa.isDirect());
        assertFalse(grupo.isDirect());
    }

    @Test
    @DisplayName("Condicional B: actualizar solo el nombre conserva la visibilidad")
    void actualizarSoloNombreConservaVisibilidad() {
        // Business / User Story Rational (US23): editar el nombre del grupo no
        // debe alterar otras propiedades como la visibilidad.
        // Arrange
        var grupo = grupoConUnMiembroExtra();
        // Act
        grupo.updateGroup("Proyecto Beta", null, null);
        // Assert
        assertEquals("Proyecto Beta", grupo.getName());
        assertEquals(GroupVisibility.PUBLIC, grupo.getVisibility());
    }

    @Test
    @DisplayName("Integridad de datos: agregar un miembro nuevo incrementa el conteo")
    void agregarMiembroIncrementaConteo() {
        // Business / User Story Rational (US23): la lista de miembros refleja con
        // exactitud quién participa en el grupo.
        // Arrange
        var grupo = grupoConUnMiembroExtra();
        var nuevo = new UserId(UUID.randomUUID());
        // Act
        grupo.addMember(nuevo);
        // Assert
        assertEquals(3, grupo.getMemberCount());
        assertTrue(grupo.isMember(nuevo));
    }
}
