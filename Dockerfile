FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (for better caching)
RUN mvn dependency:go-offline

# Copy all source files
COPY src ./src

# Create an empty Firebase file if it doesn't exist to prevent build failures
RUN mkdir -p src/main/resources/ && \
    touch src/main/resources/firebase-service-account.json && \
    echo '{}' > src/main/resources/firebase-service-account.json

# Build the application
RUN mvn clean package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/*.jar app.jar
# Make sure it gets environment variables
ENV PORT=8080
ENV JAVA_OPTS="-XX:+UseContainerSupport"
EXPOSE $PORT
# Add health check
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:$PORT/actuator/health || exit 1
# Command with proper environment variable support
CMD java $JAVA_OPTS -jar app.jar 