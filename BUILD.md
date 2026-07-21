# Build Instructions

## Prerequisites
- Java 21 JDK
- Maven 3.8.1+
- Docker & Docker Compose (optional, for containerized deployment)

## Building the Project

### Clean Build
```bash
mvn clean install
```

### Skip Tests (for faster builds)
```bash
mvn clean install -DskipTests
```

### Run Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=AccountServiceTest

# With coverage report
mvn clean test jacoco:report
```

## Running the Application

### Using Maven
```bash
mvn spring-boot:run
```

### Using Docker Compose (Complete Stack)
```bash
# Start all services (PostgreSQL, Redis, App)
docker-compose up -d

# Build and start
docker-compose up -d --build

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Using Docker (Application Only)
```bash
# Build Docker image
docker build -t banking-payment-service:1.0.0 .

# Run container
docker run -d \
  --name banking-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/banking_db \
  -e SPRING_DATASOURCE_USERNAME=banking_user \
  -e SPRING_DATASOURCE_PASSWORD=banking_password \
  -e SPRING_REDIS_HOST=redis \
  -e SPRING_REDIS_PORT=6379 \
  banking-payment-service:1.0.0
```

## Accessing the Application

Once running, access the following endpoints:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/v3/api-docs
- **Actuator**: http://localhost:8080/actuator
- **Health**: http://localhost:8080/actuator/health
- **PGAdmin**: http://localhost:5050 (if using docker-compose)

## Database Setup

### Using Docker Compose (Automatic)
The docker-compose file automatically sets up PostgreSQL with the required database and user.

### Manual Setup (Local Development)
```sql
-- Create database
CREATE DATABASE banking_db;

-- Create user
CREATE USER banking_user WITH PASSWORD 'banking_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE banking_db TO banking_user;
```

## Environment Variables

### Application Configuration
```bash
# Database
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/banking_db
export SPRING_DATASOURCE_USERNAME=banking_user
export SPRING_DATASOURCE_PASSWORD=banking_password

# Redis
export SPRING_REDIS_HOST=localhost
export SPRING_REDIS_PORT=6379

# JWT
export JWT_SECRET=your-secret-key-here
export JWT_EXPIRATION=86400000

# Server
export SERVER_PORT=8080

# Profile (dev/test/prod)
export SPRING_PROFILES_ACTIVE=dev
```

## Troubleshooting

### Connection Refused - PostgreSQL
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Start PostgreSQL container
docker-compose up -d postgres
```

### Connection Refused - Redis
```bash
# Check if Redis is running
docker ps | grep redis

# Start Redis container
docker-compose up -d redis
```

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Database Migration Issues
```bash
# Reset database (development only)
docker-compose down -v
docker-compose up -d postgres
```

## Performance Tips

1. **Enable SQL Logging** (development)
   ```yaml
   logging:
     level:
       org.hibernate.SQL: DEBUG
       org.hibernate.type.descriptor.sql.BasicBinder: TRACE
   ```

2. **Use Database Connection Pool**
   - HikariCP is configured by default
   - Adjust pool size in application.yml

3. **Monitor with Actuator**
   ```bash
   curl http://localhost:8080/actuator/metrics
   ```

4. **Check Cache Hit Ratio**
   ```bash
   redis-cli INFO stats
   ```

## Package as JAR

```bash
# Build executable JAR
mvn clean package

# Run JAR
java -jar target/banking-payment-service-1.0.0.jar

# Run with custom properties
java -Dspring.profiles.active=prod \
     -Dspring.datasource.url=jdbc:postgresql://prod-db:5432/banking_db \
     -jar target/banking-payment-service-1.0.0.jar
```

## CI/CD Integration

### Maven Central Deployment
```bash
mvn clean deploy -P release
```

### Generate Documentation
```bash
# JavaDoc
mvn javadoc:javadoc

# Test Report
mvn surefire-report:report
```

## Additional Commands

```bash
# Check dependency tree
mvn dependency:tree

# Update dependencies
mvn versions:display-dependency-updates

# Format code
mvn spotless:apply

# Run security scan
mvn dependency-check:check
```

---

**Build Guide Information**
- **Created**: January 2024
- **Last Updated**: 2024-01-15
- **Java Version**: 21
- **Maven Version**: 3.8.1+
