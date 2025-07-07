# Kafka Calculator Project

## Description
A modular Spring Boot project that performs basic arithmetic operations through a REST API using Apache Kafka for inter-module communication.

## Modules
- **rest**: Exposes HTTP GET endpoints to submit calculation requests (e.g., `/sum?a=1&b=2`).
- **calculator**: Listens to Kafka requests and returns results.

## Tech Stack
- Java 17
- Spring Boot
- Apache Kafka
- Gradle
- Docker & Docker Compose
- SLF4J with Logback for logging
- JUnit for testing

## Prerequisites
- Java 17 JDK installed
- Docker and Docker Compose installed (if using containerized environment)
- Kafka broker running locally or accessible remotely (Docker Compose will set this up automatically)

## Build
```bash
./gradlew clean build
```

## Build with Docker Compose
```bash
docker-compose build
```

## Start All Services with Docker Compose
```bash
docker-compose up -d
```

## Stop All Services with Docker Compose
```bash
docker-compose down
```

## Run Locally (Without Docker)
# Run REST API
```bash
cd rest
./gradlew bootRun

# In a separate terminal, run Calculator service
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
- `calc-requests` used by REST module to send operations
- `calc-responses` used by calculator module to send results

## Ports
- REST API: `http://localhost:8080`
- Kafka: `http://localhost:9092` (internal)
- Zookeeper: `http://localhost:2181` (internal)

## Project Structure
.
├── calculator/            # Kafka listener & calculation logic
│   └── Dockerfile
├── rest/                  # REST API that produces Kafka messages
│   └── Dockerfile
├── build.gradle           # Root Gradle config
├── docker-compose.yml     # Service orchestration
├── README.md
├── .gitignore

## Notes
- Configuration is handled via `application.properties`
- Communication between modules is 100% Kafka-based
- No XML configuration used

## Viewing Logs
# For calculator logs
```bash
cat calculator-logs/app.log
```

# For REST logs
```bash
cat rest-logs/app.log
```

## Git Usage
```bash
git clone <repo-url>
cd project
```

## Troubleshooting
- If tests are not detected, ensure test files are located in src/test/java/... with correct package structure.
- Make sure Kafka is accessible for modules if running locally.
- Ensure no other services are occupying ports 8080, 9092, or 2181.