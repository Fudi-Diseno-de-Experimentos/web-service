# Description: Como gerente, quiero marcar anuncios como prioritarios para que los empleados los vean primero.

Feature: Anuncios prioritarios

Escenario: Marcar un anuncio como prioritario
Dado que el gerente está creando un nuevo anuncio,
Cuando marca el anuncio como prioritario y completa la publicación,
Entonces el sistema muestra el anuncio en la sección destacada,
Y envía una notificación urgente a todos los empleados.
