MANUAL DE DESPLIEGUE - PROYECTO NUCLEO
======================================

Objetivo
--------
Este manual explica como ejecutar el proyecto completo usando Docker:

- Base de datos PostgreSQL
- Backend Spring Boot
- Frontend web

Al finalizar, la aplicacion debe poder verse en:

http://localhost:5173


1. Requisitos previos
---------------------
La persona que va a ejecutar el proyecto debe tener instalado:

1. Docker Desktop
2. Git
3. Una terminal de Windows: PowerShell o CMD

Recomendado: usar PowerShell.


2. Instalar Docker Desktop
--------------------------
1. Descargar Docker Desktop desde:

   https://www.docker.com/products/docker-desktop/

2. Instalar Docker Desktop.

3. Reiniciar el computador si el instalador lo solicita.

4. Abrir Docker Desktop desde el menu inicio.

5. Esperar a que Docker Desktop indique que esta corriendo.
   Normalmente debe aparecer un estado similar a:

   Docker Desktop is running

6. Verificar desde PowerShell o CMD:

   docker version

   El comando debe mostrar informacion de Client y Server.
   Si solo muestra Client, o aparece un error como "Docker Desktop is unable to start",
   Docker Desktop no esta iniciado correctamente.


3. Instalar Git
---------------
1. Descargar Git desde:

   https://git-scm.com/downloads

2. Instalarlo usando las opciones por defecto.

3. Verificar desde PowerShell o CMD:

   git --version


4. Obtener el proyecto
----------------------
Opcion A: clonar desde GitHub o repositorio remoto

1. Abrir PowerShell o CMD.

2. Ir a la carpeta donde se quiere descargar el proyecto:

   cd C:\Users\TU_USUARIO\Desktop

3. Clonar el repositorio:

   git clone URL_DEL_REPOSITORIO

4. Entrar a la carpeta del proyecto:

   cd ProyectoNucleo


Opcion B: si el proyecto fue enviado como carpeta comprimida

1. Descomprimir el archivo.

2. Abrir PowerShell o CMD.

3. Entrar a la carpeta raiz del proyecto.

   Ejemplo:

   cd C:\Users\TU_USUARIO\Desktop\ProyectoNucleo


IMPORTANTE:
-----------
Los comandos de Docker deben ejecutarse desde la carpeta donde esta el archivo:

docker-compose.yml

En este proyecto, ese archivo esta en la raiz de ProyectoNucleo.


5. Levantar la aplicacion con Docker
------------------------------------
Desde la carpeta raiz del proyecto, ejecutar:

docker compose up -d --build

Este comando hace lo siguiente:

- Descarga las imagenes necesarias si no existen en el computador.
- Construye la imagen del backend.
- Construye la imagen del frontend.
- Crea y levanta la base de datos PostgreSQL.
- Espera a que PostgreSQL este saludable.
- Levanta el backend.
- Levanta el frontend.

Durante el proceso puede tardar varios minutos la primera vez.


6. Resultado esperado del arranque
----------------------------------
Al finalizar correctamente, Docker debe mostrar mensajes parecidos a:

Container horarios-postgres Healthy
Container horarios-backend Started
Container horarios-frontend Started

Para verificar el estado de los servicios, ejecutar:

docker compose ps

El resultado debe mostrar algo similar a:

NAME                STATUS
horarios-postgres   Up ... (healthy)
horarios-backend    Up ...
horarios-frontend   Up ...


7. Abrir la aplicacion
----------------------
Cuando los contenedores esten arriba, abrir el navegador y entrar a:

http://localhost:5173

El backend queda disponible en:

http://localhost:8080

La base de datos queda disponible localmente en:

localhost:5432


8. Apagar la aplicacion
-----------------------
Desde la carpeta raiz del proyecto, ejecutar:

docker compose down

Esto apaga los contenedores, pero conserva la base de datos en el volumen de Docker.


9. Volver a iniciar la aplicacion
---------------------------------
Si ya se habia construido antes, se puede iniciar con:

docker compose up -d

Si hubo cambios en el codigo, usar:

docker compose up -d --build


10. Ver logs si algo falla
--------------------------
Ver logs de todos los servicios:

docker compose logs

Ver logs del backend:

docker logs horarios-backend

Ver logs de la base de datos:

docker logs horarios-postgres

Ver logs del frontend:

docker logs horarios-frontend


11. Problemas comunes
---------------------

Problema:
Docker Desktop is unable to start

Solucion:
1. Abrir Docker Desktop manualmente.
2. Esperar a que indique que esta corriendo.
3. Probar:

   docker version

4. Si sigue fallando, reiniciar el computador.
5. Abrir Docker Desktop como administrador.
6. En Docker Desktop, revisar:

   Settings > General > Use the WSL 2 based engine

7. En PowerShell como administrador, ejecutar:

   wsl --update
   wsl --shutdown

8. Abrir Docker Desktop otra vez.


Problema:
dependency failed to start: container horarios-postgres is unhealthy

Solucion:
1. Revisar logs:

   docker logs horarios-postgres

2. Si el error menciona datos viejos o version de PostgreSQL, borrar los contenedores y levantar de nuevo:

   docker compose down
   docker compose up -d --build

3. Si el problema continua, borrar el volumen de PostgreSQL del proyecto:

   docker compose down -v
   docker compose up -d --build

ADVERTENCIA:
docker compose down -v borra los datos guardados en la base de datos local.
Usarlo solo si no se necesita conservar esa informacion.


Problema:
El navegador no abre http://localhost:5173

Solucion:
1. Verificar contenedores:

   docker compose ps

2. Confirmar que horarios-frontend este Up.
3. Revisar logs:

   docker logs horarios-frontend

4. Verificar que ningun otro programa este usando el puerto 5173.


Problema:
El backend no responde en http://localhost:8080

Solucion:
1. Verificar contenedores:

   docker compose ps

2. Revisar logs:

   docker logs horarios-backend

3. Confirmar que horarios-postgres este healthy.


12. Probar desde otro computador en la misma red
------------------------------------------------
Si el proyecto esta corriendo en un computador y otra persona quiere entrar desde otro PC
en la misma red:

1. En el computador donde corre Docker, buscar la IP local:

   ipconfig

2. Buscar la direccion IPv4.

   Ejemplo:

   192.168.1.25

3. Desde el otro computador, abrir:

   http://192.168.1.25:5173

4. Si no abre, revisar el Firewall de Windows y permitir conexiones al puerto 5173.


13. Comandos principales
------------------------
Levantar todo construyendo imagenes:

docker compose up -d --build

Ver estado:

docker compose ps

Ver logs:

docker compose logs

Apagar:

docker compose down

Apagar y borrar volumenes:

docker compose down -v


14. Estado correcto final
-------------------------
La aplicacion esta correctamente desplegada cuando:

1. Docker Desktop esta corriendo.
2. docker compose ps muestra:

   horarios-postgres   Up ... (healthy)
   horarios-backend    Up ...
   horarios-frontend   Up ...

3. El navegador puede abrir:

   http://localhost:5173

