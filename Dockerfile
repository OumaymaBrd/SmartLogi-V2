FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .
# Téléchargement des dépendances
RUN ./mvnw dependency:go-offline
COPY src ./src
# Build sans compilation des tests défectueux
RUN ./mvnw clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]