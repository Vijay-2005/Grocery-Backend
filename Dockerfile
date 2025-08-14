# Grocery Order Application Dockerfile
# ===============================
# This multi-stage build process creates an optimized Docker image 
# for the grocery ordering backend application.

# Stage 1: Build the application
# ------------------------------
# Use Maven with OpenJDK 17 as the build environment
FROM maven:3.8-openjdk-17 AS build

# Set the working directory for build operations
WORKDIR /app

# Copy only the dependency definitions first
# This improves build caching - dependencies won't redownload if unchanged
COPY pom.xml .

# Download all dependencies before copying source code
# This step is cached unless pom.xml changes
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src ./src



# Build the application JAR file
# Skip tests during container build - tests should run in CI/CD pipeline
RUN mvn clean package -DskipTests

# Stage 2: Runtime environment
# ---------------------------
# Use a minimal JDK image for running the application
FROM openjdk:17-slim

# Set the working directory for the application
WORKDIR /app

# Install curl for container health checks
# Clean up package lists to reduce image size
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy only the built JAR file from the build stage
# This results in a smaller final image
COPY --from=build /app/target/*.jar app.jar

# Define environment variables with defaults
# These can be overridden when running the container
ENV PORT=8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -Dserver.port=$PORT"

# Expose the application port
EXPOSE $PORT

# Configure container health check
# The application's actuator health endpoint is used to verify it's running properly
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:$PORT/api/health || exit 1

# Command to run the application
# Uses environment variables for configuration
CMD java $JAVA_OPTS -jar app.jar