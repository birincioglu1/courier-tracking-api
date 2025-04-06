# Courier Tracking API

This project is a REST API developed for a courier tracking system. It is used to track courier locations, manage stores, and calculate distances traveled by couriers.

## 🚀 Technologies

- Java 17
- Spring Boot 
- PostgreSQL
- Redis
- Docker & Docker Compose
- Maven
- Swagger/OpenAPI
- JPA
- MapStruct

## 📋 Features

- Courier management
- Store management
- Location tracking
- Distance calculation
- Redis caching
- Swagger API documentation
- Optimistic and Pessimistic Locking

## 🛠️ Installation

### Prerequisites

- Java 17
- Maven
- Docker & Docker Compose
- PostgreSQL
- Redis

### Building and Running with Maven

```bash
# Build the project
mvn clean install

# Run the project
mvn spring-boot:run
```

### Running with Docker

```bash
# Build Docker image
docker build -t courier-tracking-api .

# Start all services with Docker Compose
docker-compose up -d

# Stop services
docker-compose down
```

## 🌐 API Documentation

Swagger UI access: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 📝 API Endpoints

### Courier Operations

```bash
# Create new courier
curl -X POST http://localhost:8080/api/v1/courier \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe"}'

# Update courier location
curl -X PUT http://localhost:8080/api/v1/courier/1/location \
  -H "Content-Type: application/json" \
  -d '{
    "geoLocation": {
      "latitude": 41.0082,
      "longitude": 28.9784
    },
    "time": "2024-04-05T14:30"
  }'

# Get courier total travel distance
curl -X GET http://localhost:8080/api/v1/courier/1
```

### Store Operations

```bash
# Filter stores
curl -X GET "http://localhost:8080/api/v1/store?page=0&size=10"
```

## 🏗️ Project Structure

The project is designed following Clean Architecture principles:

```
src/
├── main/
│   ├── java/
│   │   └── com/birincioglu/couriertrackingapi/
│   │       ├── application/    # Use cases and services
│   │       ├── domain/         # Domain models and business logic
│   │       ├── infrastructure/ # External services and database
│   │       └── presentation/   # API controllers and DTOs
│   └── resources/
└── test/
```

## 🔧 Design Patterns Used

- Builder Pattern
- Singleton Pattern
- Strategy Pattern
- Repository Pattern
- Service Layer Pattern
- DTO Pattern
- Command Pattern
- Locking Pattern (Optimistic & Pessimistic)

## 📊 Database Schema

- PostgreSQL database is used
- Redis is used for caching
- JPA with versioning for optimistic locking
- Pessimistic locking for concurrent operations

## 🚨 Error Handling

- Global exception handling
- Customized error messages
- HTTP status codes
- Concurrent modification handling

## 🔒 Security

- Input validation
- Exception handling
- API security
- Concurrent access control

## 📈 Performance

- Redis caching
- Database indexing
- Optimized queries
- Locking mechanisms for concurrent operations
