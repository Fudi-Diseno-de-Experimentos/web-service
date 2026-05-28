# language: es
Característica: Publicación de anuncios
  Como gerente quiero publicar anuncios en la plataforma para que los
  empleados estén informados de las novedades de la empresa. (US10, US11)

  Escenario: Publicar un anuncio normal exitosamente
    Dado que el gerente ha iniciado sesión para publicar anuncios
    Cuando publica un anuncio con título "Cierre por feriado" y prioridad "NORMAL"
    Entonces el anuncio se guarda correctamente
    Y el anuncio aparece en el listado de la compañía

  Esquema del escenario: Publicar anuncios con distintas prioridades
    Dado que el gerente ha iniciado sesión para publicar anuncios
    Cuando publica un anuncio con título "<titulo>" y prioridad "<prioridad>"
    Entonces el anuncio se guarda correctamente
    Y la prioridad registrada es "<prioridad>"

    Ejemplos:
      | titulo            | prioridad |
      | Aviso general     | NORMAL    |
      | Cambio de horario | HIGH      |
      | Evacuación        | URGENT    |
