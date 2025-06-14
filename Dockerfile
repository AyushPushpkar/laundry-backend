# 🛠 Stage 1: Build the Kotlin Spring Boot app
FROM gradle:8.4.0-jdk17 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN ./gradlew build -x test

# 🚀 Stage 2: Run the built jar
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/laundary-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
