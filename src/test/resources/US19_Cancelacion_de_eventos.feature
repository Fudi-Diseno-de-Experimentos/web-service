# Description: Como gerente, quiero cancelar eventos cuando sea necesario para evitar confusiones.

Feature: Cancelación de eventos

Escenario: Cancelar evento programado
Dado que el gerente visualiza un evento futuro en la lista de eventos,
Cuando selecciona un evento para cancelarlo y proporciona una razón,
Entonces el sistema notifica automáticamente a todos los invitados sobre la cancelación,
Y elimina el evento de la lista de eventos.
