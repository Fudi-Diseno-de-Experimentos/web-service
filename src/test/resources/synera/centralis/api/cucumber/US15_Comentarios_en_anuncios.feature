# Description: Como empleado, quiero dar feedback sobre anuncios para aclarar dudas o hacer comentarios.

Feature: Comentarios en anuncios

Scenario: Comentar un anuncio
Given que el empleado está visualizando un anuncio sobre nuevas políticas,
When selecciona el anuncio y escribe su pregunta,
Then el sistema publica el comentario asociado al anuncio,
