# Description: Como gerente, quiero publicar anuncios en la aplicación móvil para que los empleados estén informados de las novedades de la empresa.

Feature: Publicación de anuncios

Scenario: Publicar un anuncio exitosamente
Given que el gerente ha iniciado sesión en la aplicación móvil,
When quiera publicar un anuncio con información relevante,
Then el sistema guarda el anuncio en la base de datos,
And muestra el anuncio donde los empleados puedan verlo.
