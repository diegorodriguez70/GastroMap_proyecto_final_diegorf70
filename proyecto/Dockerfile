# Etapa 1: Construir el JAR con Maven
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY . .

RUN ./mvnw -DskipTests package

# Etapa 2: Ejecutar el JAR en un JDK más ligero
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
