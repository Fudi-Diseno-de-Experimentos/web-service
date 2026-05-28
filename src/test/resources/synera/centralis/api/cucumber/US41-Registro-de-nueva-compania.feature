# language: es
Característica: Registro de compañía
  Como gerente quiero registrar mi compañía en la plataforma para empezar a
  gestionar la comunicación de mi equipo. (US41)

  Escenario: Registrar una compañía exitosamente
    Dado que el representante legal está autenticado
    Cuando registra la compañía "Synera SAC" con RUC "20123456789"
    Entonces la compañía se registra correctamente

  Esquema del escenario: Registrar compañías con distintos RUC
    Dado que el representante legal está autenticado
    Cuando registra la compañía "<nombre>" con RUC "<ruc>"
    Entonces la compañía se registra correctamente
    Y el nombre registrado es "<nombre>"

    Ejemplos:
      | nombre       | ruc         |
      | Acme Perú    | 20111111111 |
      | Globex SAC   | 20222222222 |
      | Initech EIRL | 20333333333 |
