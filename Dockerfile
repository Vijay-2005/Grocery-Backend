FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (for better caching)
RUN mvn dependency:go-offline

# Copy all source files
COPY src ./src

# Create a minimal valid Firebase file if it doesn't exist to prevent build failures
RUN mkdir -p src/main/resources/ && \
    if [ ! -f src/main/resources/firebase-service-account.json ]; then \
      echo '{"type":"service_account","project_id":"dummy-project","private_key_id":"dummy","private_key":"dummy","client_email":"dummy@example.com","client_id":"dummy","auth_uri":"https://accounts.google.com/o/oauth2/auth","token_uri":"https://oauth2.googleapis.com/token","auth_provider_x509_cert_url":"https://www.googleapis.com/oauth2/v1/certs","client_x509_cert_url":"dummy"}' > src/main/resources/firebase-service-account.json; \
    fi

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