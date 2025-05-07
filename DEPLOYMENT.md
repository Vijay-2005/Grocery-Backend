# Comprehensive Deployment Guide for Grocery Backend

This guide provides detailed instructions for deploying the grocery backend application to cloud platforms or using Docker.

## 🌥️ Deployment to Render.com

Render.com offers a simple cloud platform for hosting web services with automatic deployments from Git.

### Prerequisites
- A Render.com account (free tier available)
- Your code pushed to a Git repository (GitHub, GitLab, etc.)
- A Firebase service account key file (for authentication)

### Detailed Deployment Steps

1. **Prepare Your Repository**
   - Ensure your repository contains the `render.yaml` file at the root
   - Verify that your Firebase configuration file is properly set up
   - Make sure all environment variables are documented

2. **Deploy on Render.com**
   - Log in to your Render.com account at [dashboard.render.com](https://dashboard.render.com)
   - Click on "New" and select "Blueprint" from the dropdown menu
   - Connect your Git repository that contains the grocery backend code
   - Render will automatically detect the `render.yaml` file and set up the services
   - Review the service configuration and click "Apply"
   - Wait for the deployment to complete (typically 5-10 minutes for first deploy)

3. **Post-Deployment Configuration**
   - Once deployed, navigate to the "Environment" tab of your service
   - Verify that all environment variables are correctly set
   - Set up any required secrets, such as database credentials or API keys

The application will be available at the URL provided by Render once the deployment is finished (typically in the format `https://your-service-name.onrender.com`).

## 🔑 Environment Variables

The following environment variables are configured in render.yaml and can be customized for your deployment:

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `DATABASE_URL` | MySQL database connection string | Railway MySQL instance |
| `MYSQL_USERNAME` | Database username | root |
| `MYSQL_PASSWORD` | Database password | (Must be set) |
| `SPRING_PROFILES_ACTIVE` | Spring profile to activate | prod |
| `DEVELOPMENT_MODE` | Whether to bypass authentication | false |
| `ALLOWED_ORIGINS` | CORS allowed origins | * |

These values can be modified in the Render dashboard under the "Environment" tab of your service.

## 🐳 Local Deployment with Docker Compose

Docker Compose provides a simple way to run the application with its database locally.

### Prerequisites
- Docker and Docker Compose installed on your development machine
- Java 17 and Maven for local development (optional)

### Step-by-Step Instructions

1. **Clone and Navigate to Project**
   ```bash
   git clone https://github.com/yourusername/grocery-backend.git
   cd grocery-backend
   ```

2. **Configure Firebase**
   - Place your Firebase service account JSON file at:
     `src/main/resources/firebase-service-account.json`

3. **Start the Application Stack**
   ```bash
   docker-compose up -d
   ```
   This command:
   - Builds the application container from the Dockerfile
   - Creates a PostgreSQL database container
   - Sets up networking between containers
   - Configures persistent volume for database data

4. **Verify Deployment**
   - The application will be available at http://localhost:8080
   - Test the health endpoint: http://localhost:8080/api/health

### Managing the Application

- **View logs**:
  ```bash
  docker-compose logs -f app
  ```

- **Stop the application stack**:
  ```bash
  docker-compose down
  ```

- **Restart with rebuilt containers**:
  ```bash
  docker-compose up -d --build
  ```

## 🐋 Manual Deployment with Docker

If you need more control over the deployment, you can use Docker directly:

1. **Build the Docker image**:
   ```bash
   docker build -t grocery-backend .
   ```

2. **Run the container**:
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=prod \
     -e DATABASE_URL=your-database-url \
     -e MYSQL_USERNAME=your-username \
     -e MYSQL_PASSWORD=your-password \
     grocery-backend
   ```

3. **Connect to an external database**:
   Make sure your database is:
   - Accessible from the container
   - Has the correct schema created
   - Has required permissions for the provided user

## 🔥 Firebase Configuration

Firebase is used for authentication in this application:

1. **Create a Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use an existing one
   - Enable Authentication with the methods you need (email/password, Google, etc.)

2. **Generate Service Account Key**:
   - In Firebase Console, go to Project Settings → Service Accounts
   - Click "Generate New Private Key"
   - Save the JSON file to `src/main/resources/firebase-service-account.json`

3. **Secure Your Key**:
   - Never commit this key to version control
   - In production, consider using environment variables or secrets management

## 🔍 Troubleshooting

### Health Checking
Monitor your application health using these endpoints:
- `/actuator/health` - Detailed Spring Boot health information
- `/api/health` - Simple application health status

### Container Logs
To view detailed logs from the Docker container:
```bash
docker logs grocery-backend
```

### Common Issues and Solutions

1. **Database Connection Issues**:
   - Verify the database credentials in environment variables
   - Ensure the database server is accessible from your application
   - Check if the database user has sufficient permissions

2. **Authentication Failures**:
   - Verify that your Firebase service account file is correctly formatted
   - Check that the Firebase project is properly configured
   - Ensure the client is sending the correct authorization header

3. **Container Startup Failures**:
   - Check logs with `docker logs grocery-backend`
   - Verify that all required environment variables are set
   - Ensure the container has sufficient resources (CPU/memory)

4. **Network Issues**:
   - Make sure required ports are open in any firewalls
   - Check that container networking is properly configured
   - Verify DNS resolution if using hostnames in configuration