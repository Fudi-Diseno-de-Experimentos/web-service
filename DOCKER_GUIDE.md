# Scripts para Docker - Centralis Web Services

## 🐳 Comandos Docker para el Proyecto Centralis

### 📋 **Prerequisitos:**
- Docker Desktop instalado
- Docker Compose instalado

---

### 🚀 **1. Construir y Ejecutar con Docker Compose (Recomendado)**

```bash
# Construir y ejecutar en primer plano
docker-compose up --build

# Construir y ejecutar en segundo plano
docker-compose up --build -d

# Ver logs en tiempo real
docker-compose logs -f centralis-app

# Parar la aplicación
docker-compose down
```

---

### 🔨 **2. Comandos Docker Manuales**

```bash
# Construir la imagen
docker build -t centralis-web-services:latest .

# Ejecutar el contenedor
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:6543/postgres?sslmode=require" \
  -e SPRING_DATASOURCE_USERNAME="postgres.qelofvejjwmgmijkmfyd" \
  -e SPRING_DATASOURCE_PASSWORD="Uzb2IfIYqfnRKVnz" \
  --name centralis-container \
  centralis-web-services:latest

# Ver logs del contenedor
docker logs -f centralis-container

# Parar el contenedor
docker stop centralis-container

# Eliminar el contenedor
docker rm centralis-container
```

---

### 🔍 **3. Comandos de Debugging**

```bash
# Entrar al contenedor en ejecución
docker exec -it centralis-container /bin/bash

# Ver procesos dentro del contenedor
docker exec centralis-container ps aux

# Ver variables de entorno
docker exec centralis-container env

# Inspeccionar la imagen
docker inspect centralis-web-services:latest
```

---

### 📊 **4. Monitoreo y Health Check**

```bash
# Verificar estado de salud
curl http://localhost:8080/actuator/health

# Ver métricas (si están habilitadas)
curl http://localhost:8080/actuator/metrics

# Verificar que la aplicación responde
curl http://localhost:8080/api/v1/health
```

---

### 🧹 **5. Limpieza de Docker**

```bash
# Limpiar contenedores parados
docker container prune

# Limpiar imágenes no utilizadas
docker image prune

# Limpiar todo (¡Cuidado!)
docker system prune -a

# Ver espacio usado por Docker
docker system df
```

---

### ⚙️ **6. Variables de Entorno Disponibles**

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring | `docker` |
| `SPRING_DATASOURCE_URL` | URL de Supabase | Configurada |
| `SPRING_DATASOURCE_USERNAME` | Usuario de DB | `postgres.qelofvejjwmgmijkmfyd` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de DB | Tu contraseña |
| `SPRING_JPA_SHOW_SQL` | Mostrar SQL | `true` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | DDL Auto | `update` |

---

### 🛠️ **7. Troubleshooting**

```bash
# Si el build falla, limpiar cache de Maven
docker build --no-cache -t centralis-web-services:latest .

# Si hay problemas de conectividad
docker run --rm -it centralis-web-services:latest /bin/bash
# Dentro del contenedor:
ping aws-1-us-east-2.pooler.supabase.com
nslookup aws-1-us-east-2.pooler.supabase.com

# Verificar logs detallados
docker-compose logs --tail=100 centralis-app
```

---

### 📝 **8. Archivos Importantes**

- `Dockerfile` - Definición de la imagen
- `docker-compose.yml` - Orquestación de servicios
- `.dockerignore` - Archivos a ignorar en el build
- `application-docker.properties` - Configuración para Docker

---

### 🎯 **9. Comandos Rápidos de Desarrollo**

```bash
# Desarrollo: Rebuild y restart rápido
docker-compose down && docker-compose up --build -d

# Ver logs de errores solamente
docker-compose logs centralis-app | grep ERROR

# Restart sin rebuild
docker-compose restart centralis-app
```