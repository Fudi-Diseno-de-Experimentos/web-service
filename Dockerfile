# Usar Eclipse Temurin Java 24 como imagen base
FROM eclipse-temurin:24-jdk

# Información del mantenedor
LABEL maintainer="synera-centralis"

# Crear directorio de trabajo
WORKDIR /app

# Copiar el archivo Maven wrapper y pom.xml primero (para cache de dependencias)
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Descargar dependencias (esto se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer el puerto 8080
EXPOSE 8080

# Ejecutar la aplicación
CMD ["java", "-jar", "target/web-services-0.0.1-SNAPSHOT.jar"]