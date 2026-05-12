# Description: Como gerente, quiero marcar anuncios como prioritarios para que los empleados los vean primero.

Feature: Anuncios prioritarios

Scenario: Marcar un anuncio como prioritario
Given que el gerente está creando un nuevo anuncio,
When marca el anuncio como prioritario y completa la publicación,
Then el sistema muestra el anuncio en la sección destacada,
And envía una notificación urgente a todos los empleados.
