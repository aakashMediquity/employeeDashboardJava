FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/employee-api-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
