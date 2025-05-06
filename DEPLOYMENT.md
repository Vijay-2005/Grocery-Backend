# Deployment Guide for Grocery Backend

This guide will help you deploy the grocery backend application to Render.com or using Docker.

## Deployment to Render.com

### Prerequisites
- A Render.com account
- Your code pushed to a Git repository (GitHub, GitLab, etc.)

### Steps

1. Log in to your Render.com account
2. Click on "New" and select "Blueprint" from the dropdown menu
3. Connect your Git repository that contains the grocery backend code
4. Render will automatically detect the `render.yaml` file and set up the services
5. Review the service configuration and click "Apply"
6. Wait for the deployment to complete

The application will be available at the URL provided by Render once the deployment is finished.

## Environment Variables

You may need to set up the following environment variables in your Render.com dashboard:

- `SPRING_DATASOURCE_URL`: Your database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `FIREBASE_CONFIG`: Your Firebase configuration (if using Firebase Auth)

## Local Deployment with Docker

### Prerequisites
- Docker and Docker Compose installed on your machine

### Steps

1. Navigate to the project root directory
2. Build and start the containers:
   ```
   docker-compose up -d
   ```
3. The application will be available at http://localhost:8080

### Stopping the Application
```
docker-compose down
```

## Manual Deployment with Docker

If you want to deploy just the Docker container without Docker Compose:

1. Build the Docker image:
   ```
   docker build -t grocery-backend .
   ```

2. Run the container:
   ```
   docker run -p 8080:8080 grocery-backend
   ```

## Troubleshooting

### Application Health Check
You can check the health of your application by visiting `/actuator/health` endpoint.

### Container Logs
To view logs from the Docker container:
```
docker logs grocery-backend
```

### Database Connection Issues
If you encounter database connection issues, verify the database credentials and make sure the database server is accessible from your application. 