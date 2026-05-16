# Description: Como empleado, quiero crear chats grupales para discutir temas específicos con mis colegas.

Feature: Creación de chats grupales

Scenario: Crear chat grupal para proyecto
Given que el empleado necesita coordinar un proyecto con un equipo,
When crea un nuevo chat, añade participantes y establece un nombre para el grupo,
Then el sistema crea el chat con todos los miembros añadidos.
