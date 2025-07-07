# Use Eclipse Temurin's lightweight OpenJDK 17 runtime image based on Alpine Linux
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the built jar file from the host to the container's working directory
COPY build/libs/rest-*.jar app.jar

# Run the application using the java -jar command when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]