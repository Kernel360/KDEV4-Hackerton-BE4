# Step 1: Use an official OpenJDK 17 base image
FROM openjdk:17-jdk-slim as build

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the built Spring Boot jar file from the host machine into the container
COPY build/libs/be4-0.0.1-SNAPSHOT.jar /app/be4-0.0.1-SNAPSHOT.jar

# Step 4: Expose port 8080 (or whichever port you want your app to listen on)
EXPOSE 8070

# Step 5: Define the command to run the jar file
ENTRYPOINT ["java", "-jar", "/app/be4-0.0.1-SNAPSHOT.jar"]
