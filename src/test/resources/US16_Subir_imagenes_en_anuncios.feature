# Description: Como gerente quiero poder adjuntar imágenes a los anuncios publicados, para que la información sea más clara y atractiva.

Feature: Subir imágenes en anuncios

Escenario: Adjuntar imagen a un anuncio
Dado que un gerente está creando un anuncio,
Cuando selecciona la opción de adjuntar una imagen desde su dispositivo,
Entonces la imagen se carga en Cloudinary y queda asociada al anuncio.
