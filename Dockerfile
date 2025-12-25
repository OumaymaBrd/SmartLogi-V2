FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

RUN apt-get update && apt-get install -y dos2unix

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN dos2unix mvnw && chmod +x mvnw

RUN ./mvnw dependency:go-offline
COPY src ./src

RUN ./mvnw clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9089
ENTRYPOINT ["java", "-jar", "app.jar"]