FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

RUN apt-get update && apt-get install -y dos2unix && rm -rf /var/lib/apt/lists/*

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN dos2unix mvnw && chmod +x mvnw

# Cela permet de mettre en cache les dépendances et accélérer les builds futurs
RUN ./mvnw dependency:go-offline -B || true

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Étape finale (JRE plus légère)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9089
ENTRYPOINT ["java", "-jar", "app.jar"]
