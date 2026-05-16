# Description: Como empleado, quiero editar mensajes que ya envié para corregir errores tipográficos.

Feature: Edición de mensajes

Scenario: Corregir error tipográfico en mensaje
Given que el empleado envió un mensaje con un error de ortografía,
When selecciona el mensaje y realiza la corrección,
Then el sistema actualiza el contenido del mensaje.
