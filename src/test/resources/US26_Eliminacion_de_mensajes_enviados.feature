# Description: Como empleado, quiero eliminar mensajes que envié por error para corregir mis equivocaciones.

Feature: Eliminación de mensajes propios

Escenario: Eliminar mensaje recién enviado
Dado que el empleado envió un mensaje con información incorrecta,
Cuando selecciona el mensaje y lo elimina,
Entonces el sistema remueve el mensaje para todos los participantes.
