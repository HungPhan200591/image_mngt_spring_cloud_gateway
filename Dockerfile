# Use an official OpenJDK runtime as a parent image
FROM amazoncorretto:22.0.2-alpine3.20

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/image_mngt_spring_cloud_gateway-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 8082

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]