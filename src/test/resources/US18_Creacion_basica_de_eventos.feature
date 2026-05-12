# Description: Como gerente, quiero crear eventos en la aplicación móvil para organizar reuniones y actividades de la empresa.

Feature: Creación de eventos

Escenario: Crear un evento exitosamente
Dado que el gerente ha iniciado sesión en la aplicación móvil,
Cuando crea un evento llenando los datos necesarios,
Entonces el sistema guarda el evento en la base de datos,
Y lo muestra a los empleados seleccionados.
