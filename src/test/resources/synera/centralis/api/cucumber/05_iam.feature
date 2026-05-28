# language: es
Característica: Restricción de acceso a la API
  Como desarrollador quiero proteger las rutas de la API para bloquear el
  acceso no autorizado. (US34)

  Escenario: Un usuario autenticado accede a un recurso protegido
    Dado que un empleado autenticado solicita el recurso de compañías
    Cuando consulta el listado de compañías
    Entonces obtiene una respuesta exitosa

  Escenario: Una solicitud sin autenticación es rechazada
    Dado que un visitante sin token solicita el recurso de compañías
    Cuando consulta el listado de compañías
    Entonces la solicitud es rechazada por falta de autenticación
