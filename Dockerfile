
# 🛠 Stage 1: Build the Kotlin Spring Boot app
FROM gradle:8.4.0-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .

# 🧼 Ensure gradlew has execute permission
RUN chmod +x ./gradlew

# ✅ Use bootJar for correct Spring Boot output
RUN ./gradlew bootJar -x test

# 🚀 Stage 2: Run the built jar
FROM eclipse-temurin:21-jdk
WORKDIR /app

# ✅ Install CA certificates (critical for HTTPS)
RUN apt-get update && \
    apt-get install -y ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/laundary-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]



