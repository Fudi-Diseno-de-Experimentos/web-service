# Description: Como empleado quiero poder enviar imágenes en los chats grupales, para compartir información visual con mi equipo.

Feature: Enviar imágenes en chats grupales

Escenario: Enviar imagen en un chat grupal
Dado que un usuario participa en un chat grupal,
Cuando adjunta y envía una imagen,
Entonces el sistema sube la imagen a Cloudinary y la muestra en la conversación.
Escenario: Error al enviar imagen
Dado que un usuario participa en un chat,
Cuando intenta enviar una imagen y ocurre un error de carga,
Entonces el sistema muestra un mensaje de error y le permite reintentar.
