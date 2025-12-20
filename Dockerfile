# Étape 1 : Build de l'application avec Maven Wrapper
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copier le Maven Wrapper et les fichiers de configuration
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Copier le code source
COPY src ./src

# Exécuter le build avec Maven Wrapper
RUN ./mvnw clean package -DskipTests

# Étape 2 : Créer l'image finale avec le JDK pour exécuter l'application
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copier le jar buildé depuis l'étape précédente
COPY --from=build /app/target/*.jar app.jar

# Créer le dossier pour les logs (monté par le volume)
RUN mkdir -p /app/logs

# Exposer le port Spring Boot
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
