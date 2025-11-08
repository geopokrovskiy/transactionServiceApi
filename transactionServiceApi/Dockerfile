FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
EXPOSE 5050