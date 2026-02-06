# �tape 1 : Construction (Build)
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
# Copie des sources
COPY src ./src
# Build du JAR
RUN mvn clean package -DskipTests

# �tape 2 : Ex�cution (Run)
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Force l'utilisation du port fourni par Render ou 8081 par d�faut
EXPOSE 8081
CMD ["java", "-jar", "app.jar", "--server.port=${PORT:-8081}"]
