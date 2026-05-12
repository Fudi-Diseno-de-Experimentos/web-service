# Description: Como gerente, quiero editar anuncios ya publicados para corregir errores o actualizar información.

Feature: Editar anuncio
Escenario: Editar un anuncio existente
Dado que el gerente visualiza un anuncio publicado previamente,
Cuando modifica y guarda los cambios de la nueva información del anuncio,
Entonces el sistema actualiza el anuncio en la base de datos,
Y los cambios se reflejan inmediatamente.
