# Kafka Calculator Project

## Description
A modular Spring Boot project that performs basic arithmetic operations through a REST API using Apache Kafka for inter-module communication.

## Modules
- **rest**: Exposes HTTP GET endpoints to submit calculation requests.
- **calculator**: Listens to Kafka requests and returns results.

## Tech Stack
- Java 17
- Spring Boot
- Apache Kafka
- Gradle
- Docker & Docker Compose
- SLF4J with Logback for logging

## Prerequisites
- Java 17 JDK installed
- Docker and Docker Compose installed (if using containerized environment)
- Kafka broker running locally or accessible remotely (Docker Compose will set this up automatically)

## Build
```bash
./gradlew clean build
```

## build with Docker Compose
```bash
docker-compose --build
```

## Start with Docker Compose
```bash
docker-compose up -d
```

## Run Locally (Without Docker)
```bash
cd rest
./gradlew bootRun

cd ../calculator
./gradlew bootRun
```

## API Usage
```http
GET http://localhost:8080/sum?a=1&b=2
```
Response:
```json
{
  "result": 3.0
}
```

## Testing
```bash
./gradlew test 
```

## Kafka Topics
- `calc-requests`
- `calc-responses`

## Notes
- Configuration is handled via `application.properties`
- Communication between modules is 100% Kafka-based
- No XML configuration used

## Git Usage
```bash
git clone <repo-url>
cd project
```

## Troubleshooting
- If tests are not detected, ensure test files are located in src/test/java/... with correct package structure.
- Make sure Kafka is accessible for modules if running locally.
- Use 'docker-compose logs' to check container logs when using Docker.