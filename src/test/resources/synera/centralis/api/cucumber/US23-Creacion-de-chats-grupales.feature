# language: es
Característica: Creación de chats grupales
  Como empleado quiero crear chats grupales para discutir temas específicos
  con mis colegas. (US23)

  Escenario: Crear un chat grupal público con miembros
    Dado que el empleado está autenticado en el chat
    Cuando crea el grupo "Proyecto Alpha" con visibilidad "PUBLIC" y los miembros:
      | miembro                              |
      | 44444444-4444-4444-4444-444444444444 |
      | 55555555-5555-5555-5555-555555555555 |
    Entonces el grupo se crea correctamente
    Y el grupo queda disponible para sus miembros

  Escenario: Crear un chat grupal privado
    Dado que el empleado está autenticado en el chat
    Cuando crea el grupo "Finanzas Internas" con visibilidad "PRIVATE" y los miembros:
      | miembro                              |
      | 66666666-6666-6666-6666-666666666666 |
    Entonces el grupo se crea correctamente
