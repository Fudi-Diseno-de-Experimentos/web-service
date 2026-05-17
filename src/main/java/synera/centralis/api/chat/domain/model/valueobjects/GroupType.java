package synera.centralis.api.chat.domain.model.valueobjects;

/**
 * Tipo de conversación de chat.
 * Diferencia un chat grupal de una conversación directa (1 a 1) estilo WhatsApp.
 */
public enum GroupType {
    /**
     * Chat grupal: varios miembros, gestionado explícitamente por los usuarios.
     */
    GROUP,

    /**
     * Conversación directa: exactamente 2 miembros de la misma compañía.
     * Se crea/recupera de forma automática (get-or-create) y no se gestiona
     * mediante los endpoints de grupos.
     */
    DIRECT
}
