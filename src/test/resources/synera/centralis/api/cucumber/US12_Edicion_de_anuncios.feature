# Description: Como gerente, quiero editar anuncios ya publicados para corregir errores o actualizar información.

Feature: Editar anuncio
Scenario: Editar un anuncio existente
Given que el gerente visualiza un anuncio publicado previamente,
When modifica y guarda los cambios de la nueva información del anuncio,
Then el sistema actualiza el anuncio en la base de datos,
And los cambios se reflejan inmediatamente.
