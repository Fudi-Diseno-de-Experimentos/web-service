# Description: Como gerente, quiero eliminar anuncios obsoletos para mantener la información actualizada.

Feature: Eliminación de anuncios

Escenario: Eliminar un anuncio publicado
Dado que el gerente visualiza un anuncio,
Cuando selecciona el anuncio a eliminar y confirma la acción,
Entonces el sistema remueve el anuncio de la base de datos,
Y los empleados ya no pueden visualizarlo en sus dispositivos.
