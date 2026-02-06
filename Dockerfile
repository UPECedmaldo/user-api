# Étape 1 : Construction (Build) - Utilisation d'une image Maven plus récente et stable
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Exécution (Run) - Passage à eclipse-temurin (recommandé pour Docker)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Render utilise la variable d'environnement PORT
EXPOSE 8081
CMD ["java", "-jar", "app.jar", "--server.port=${PORT:-8081}"]
