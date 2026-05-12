# Description: Como gerente, quiero eliminar chats grupales para mantener el orden de las conversaciones.

Feature: Moderación de chats

Escenario: Eliminar mensaje inapropiado
Dado que un empleado envía un mensaje inadecuado en un chat grupal,
Cuando el gerente selecciona el mensaje y confirma su eliminación,
Entonces el sistema remueve el mensaje para todos los participantes.
