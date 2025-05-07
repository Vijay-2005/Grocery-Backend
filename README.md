# Grocery Order Application

A Spring Boot backend service for managing grocery orders, featuring Firebase authentication, MySQL database integration, and RESTful API endpoints.

## 📋 Overview

This application provides a secure backend for a grocery ordering system with the following features:

- **User Authentication:** Firebase-based authentication system with JWT token validation
- **Order Management:** Create and retrieve order history for authenticated users
- **Database Integration:** MySQL database for persistent order storage
- **API Endpoints:** RESTful API for interacting with the order system
- **Containerization:** Docker and Docker Compose support for easy deployment
- **Environment Flexibility:** Development and production environment configurations

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (optional, for containerized deployment)
- Firebase project with service account credentials

### Local Development Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/grocery-backend.git
   cd grocery-backend
   ```

2. **Configure Firebase:**
   - Create a Firebase project at [https://console.firebase.google.com/](https://console.firebase.google.com/)
   - Generate a service account key (Project Settings → Service Accounts → Generate New Private Key)
   - Save the JSON file as `src/main/resources/firebase-service-account.json`

3. **Configure Database:**
   - Update `src/main/resources/application.properties` with your database credentials
   - Or use the provided Docker Compose setup which includes a PostgreSQL database

4. **Build and Run:**
   ```bash
   mvn spring-boot:run
   ```

5. **Development Mode:**
   - Set `app.auth.development-mode=true` in application.properties to bypass authentication in development

### Docker Deployment

1. **Build and run with Docker Compose:**
   ```bash
   docker-compose up -d
   ```

2. **Access the application:**
   - API will be available at: http://localhost:8080/api
   - Health check: http://localhost:8080/api/health

## 🔒 Authentication

This application uses Firebase Authentication:

1. Client authenticates with Firebase to obtain a JWT token
2. Client includes the token in API requests in the Authorization header:
   ```
   Authorization: Bearer <firebase-token>
   ```
3. Backend validates the token using Firebase Admin SDK
4. If valid, the user's Firebase UID is extracted and used for authorization

## 📡 API Endpoints

### Public Endpoints

- `GET /api/health` - Health check endpoint
- `GET /api/auth/status` - Authentication status information

### Protected Endpoints (require authentication)

- `POST /api/orders` - Create a new order
- `GET /api/orders/history` - Get order history for the authenticated user

### Development/Testing Endpoints

- `GET /api/orders/system-check` - Verify system components and configuration
- `GET /api/orders/create-test-order` - Create a test order (development only)

## 🛠️ Technology Stack

- **Framework:** Spring Boot 3.x
- **Security:** Spring Security with Firebase Authentication
- **Database:** MySQL (with JPA/Hibernate)
- **Build Tool:** Maven
- **Containerization:** Docker & Docker Compose
- **Java Version:** Java 17
- **Testing:** JUnit 5, Spring Boot Test

## 📦 Deployment Options

The application supports multiple deployment options:

- **Local Development:** Using Maven and IDE
- **Containerized:** Using Docker and Docker Compose
- **Cloud Platforms:** Railway, Render, AWS, GCP, Azure

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Submit a pull request