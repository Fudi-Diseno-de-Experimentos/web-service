# Description: Como gerente, quiero modificar detalles de eventos existentes para ajustar cambios de último momento.

Feature: Modificación de eventos

Escenario: Cambiar fecha de evento
Dado que el gerente necesita posponer un eventos,
Cuando edita la fecha del evento y guarda los cambios,
Entonces el sistema notifica automáticamente a los invitados sobre la nueva fecha,
Y actualiza el evento en la lista de eventos.
