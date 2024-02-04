# Use a base image with Java 21 pre-installed
FROM openjdk:21-slim

# Create a working directory within the container
WORKDIR /app

# Copy the application's jar file into the container
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port (default for Spring Boot is 8080)
EXPOSE 8080

# Run the application when the container starts
CMD [ "java", "-jar","app.jar" ]