# Description: Como gerente, quiero crear eventos en la aplicación móvil para organizar reuniones y actividades de la empresa.

Feature: Creación de eventos

Scenario: Crear un evento exitosamente
Given que el gerente ha iniciado sesión en la aplicación móvil,
When crea un evento llenando los datos necesarios,
Then el sistema guarda el evento en la base de datos,
And lo muestra a los empleados seleccionados.
