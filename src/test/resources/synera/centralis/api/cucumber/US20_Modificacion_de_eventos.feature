# Description: Como gerente, quiero modificar detalles de eventos existentes para ajustar cambios de último momento.

Feature: Modificación de eventos

Scenario: Cambiar fecha de evento
Given que el gerente necesita posponer un eventos,
When edita la fecha del evento y guarda los cambios,
Then el sistema notifica automáticamente a los invitados sobre la nueva fecha,
And actualiza el evento en la lista de eventos.
