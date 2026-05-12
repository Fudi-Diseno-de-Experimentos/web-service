# Description: Como gerente, quiero publicar anuncios en la aplicación móvil para que los empleados estén informados de las novedades de la empresa.

Feature: Publicación de anuncios

Escenario: Publicar un anuncio exitosamente
Dado que el gerente ha iniciado sesión en la aplicación móvil,
Cuando quiera publicar un anuncio con información relevante,
Entonces el sistema guarda el anuncio en la base de datos,
Y muestra el anuncio donde los empleados puedan verlo.
