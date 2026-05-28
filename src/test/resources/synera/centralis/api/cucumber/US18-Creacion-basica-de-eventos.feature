# language: es
Característica: Creación de eventos
  Como gerente quiero crear eventos para organizar reuniones y actividades de
  la empresa, y que solo el personal de gestión pueda hacerlo. (US18, US34)

  Escenario: Crear un evento con varios invitados
    Dado que el gerente está autenticado para gestionar eventos
    Cuando crea el evento "Reunión General" con los siguientes invitados:
      | invitado                             |
      | 11111111-1111-1111-1111-111111111111 |
      | 22222222-2222-2222-2222-222222222222 |
    Entonces el evento se crea correctamente
    Y el evento aparece en la lista de eventos

  Escenario: Un empleado sin permisos de gestión no puede crear eventos
    Dado que un empleado sin permisos de gestión está autenticado
    Cuando intenta crear el evento "Fiesta de fin de año" con los siguientes invitados:
      | invitado                             |
      | 33333333-3333-3333-3333-333333333333 |
    Entonces el acceso para crear el evento es denegado
