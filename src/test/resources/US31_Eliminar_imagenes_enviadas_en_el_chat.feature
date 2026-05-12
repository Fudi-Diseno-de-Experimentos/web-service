# Description: Como usuario quiero poder eliminar una imagen enviada en un chat, para corregir errores o evitar confusiones.

Feature: Eliminar imágenes enviadas en el chat

Escenario: Eliminar una imagen enviada
Dado que un usuario envió una imagen en un chat,
Cuando selecciona la opción de eliminar,
Entonces la imagen desaparece del chat y se muestra un marcador de "archivo eliminado".
Escenario: Visualizar imagen eliminada en el historial
Dado que un usuario eliminó una imagen,
Cuando otro participante revisa el historial del chat,
Entonces debe visualizar el marcador de "archivo eliminado" en lugar de la imagen.
