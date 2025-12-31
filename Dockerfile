FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Installation de dos2unix pour gérer les fichiers Windows
RUN apt-get update && apt-get install -y dos2unix

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Correction des scripts
RUN dos2unix mvnw && chmod +x mvnw

# On saute go-offline qui bloque Jenkins et on passe direct au build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Étape finale (JRE plus légère)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9089
ENTRYPOINT ["java", "-jar", "app.jar"]