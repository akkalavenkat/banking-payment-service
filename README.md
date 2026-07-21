# Banking Payment Service

A comprehensive banking and payment service built with Spring Boot 3, designed for enterprise use with production-ready features.

## Overview

This is a fully-featured banking payment service showcasing modern Spring Boot architecture with REST APIs, JWT authentication, PostgreSQL persistence, Redis caching, and comprehensive transaction management.

**Key Technologies:**
- Spring Boot 3.3.0
- Spring Security with JWT (OAuth2)
- Spring Data JPA with Hibernate
- PostgreSQL database
- Redis caching
- Docker & Docker Compose
- SpringDoc OpenAPI (Swagger UI)
- Spring Actuator for monitoring

## Architecture

```
src/main/java/com/banking/
├── BankingPaymentServiceApplication.java
├── config/           # Configuration classes
│   ├── SecurityConfig.java
│   ├── RedisConfig.java
│   └── OpenApiConfig.java
├── controller/       # REST API endpoints
│   ├── AuthController.java
│   ├── AccountController.java
│   ├── PaymentController.java
│   └── TransactionController.java
├── service/         # Business logic layer
│   ├── AccountService.java
│   ├── PaymentService.java
│   └── TransactionService.java
├── repository/      # Data access layer (Spring Data JPA)
│   ├── AccountRepository.java
│   ├── PaymentRepository.java
│   └── TransactionRepository.java
├── entity/          # JPA entities
│   ├── Account.java
│   ├── Payment.java
│   └── Transaction.java
├── dto/             # Data Transfer Objects
│   ├── AccountDTO.java
│   ├── PaymentRequest.java
│   ├── PaymentResponse.java
│   └── TransactionDTO.java
├── security/        # JWT & authentication
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
└── util/            # Utility classes
    ├── ApiResponse.java
    └── ErrorResponse.java
```

## Database Schema

### Accounts Table
- Core banking accounts with balance tracking
- Indexed by account_number and user_id for fast lookups
- Status management (ACTIVE, INACTIVE, SUSPENDED, CLOSED)
- Account types: SAVINGS, CHECKING, MONEY_MARKET, CREDIT

### Payments Table
- Payment transactions between accounts
- Reference number tracking for audit
- Status workflow: PENDING → PROCESSING → COMPLETED/FAILED
- Support for multiple payment types: TRANSFER, BILL_PAY, CHECK_DEPOSIT, ACH, WIRE_TRANSFER

### Transactions Table
- Complete audit log of all account activities
- Tracks balance snapshots
- Transaction types: DEBIT, CREDIT, TRANSFER_IN, TRANSFER_OUT, INTEREST, FEE

## API Endpoints

### Authentication
```
POST   /api/v1/auth/login       - Generate JWT token
POST   /api/v1/auth/validate    - Validate JWT token
```

### Accounts
```
GET    /api/v1/accounts                     - Get user's accounts
GET    /api/v1/accounts/{accountNumber}     - Get account details
POST   /api/v1/accounts                     - Create new account
PUT    /api/v1/accounts/{accountNumber}/deactivate - Deactivate account
```

### Payments
```
POST   /api/v1/payments                     - Process payment
GET    /api/v1/payments/{referenceNumber}   - Get payment details
GET    /api/v1/payments/account/{accountId} - Get account payment history
GET    /api/v1/payments/pending             - Get pending payments
```

### Transactions
```
GET    /api/v1/transactions/account/{accountId}        - Get account transactions
GET    /api/v1/transactions/account/{accountId}/range   - Get transactions by date range
```

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 16+
- Redis 7+

### Quick Start with Docker Compose

```bash
# Start all services
docker-compose up -d

# Check service health
docker-compose ps

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

### Local Development

1. **Start PostgreSQL and Redis:**
```bash
docker-compose up -d postgres redis
```

2. **Configure application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/banking_db
    username: banking_user
    password: banking_password
  redis:
    host: localhost
    port: 6379
```

3. **Build and run:**
```bash
mvn clean install
mvn spring-boot:run
```

4. **Access the application:**
- API Documentation: http://localhost:8080/swagger-ui.html
- Actuator: http://localhost:8080/actuator
- PGAdmin: http://localhost:5050 (admin@banking.com / admin123)

## Key Features

### 🔐 Security
- JWT-based authentication with asymmetric signing
- Spring Security with method-level authorization
- CORS configuration for API security
- Stateless session management
- Password encryption with BCrypt

### 💾 Persistence
- Spring Data JPA with Hibernate ORM
- PostgreSQL for reliable data storage
- Custom query methods with @Query annotations
- Transaction management with @Transactional
- Database indexing for performance optimization

### ⚡ Caching
- Redis-backed caching with Spring Cache abstraction
- TTL (Time To Live) configuration
- Cache invalidation with @CacheEvict
- Cacheable methods for common queries
- Async cache operations

### 💳 Payment Processing
- Multi-step payment workflow
- Atomic transactions with database ACID compliance
- Balance validation and fund verification
- Payment reference tracking
- Comprehensive audit logging

### 📊 Monitoring
- Spring Actuator endpoints
- Health checks for dependencies
- Application metrics
- Structured logging with SLF4J

### 📚 API Documentation
- OpenAPI 3.0 specification
- Interactive Swagger UI
- Request/response examples
- Security scheme documentation

## Example API Usage

### 1. Login to get JWT token
```bash
curl -X POST "http://localhost:8080/api/v1/auth/login?userId=user123&email=user@banking.com"
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "user123",
    "email": "user@banking.com",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer"
  },
  "timestamp": 1626912345000
}
```

### 2. Create accounts
```bash
curl -X POST "http://localhost:8080/api/v1/accounts?accountNumber=ACC001&accountHolder=John%20Doe&accountType=CHECKING" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Process payment
```bash
curl -X POST "http://localhost:8080/api/v1/payments" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "from_account_number": "ACC001",
    "to_account_number": "ACC002",
    "amount": 100.50,
    "payment_type": "TRANSFER",
    "description": "Payment to friend"
  }'
```

### 4. Get account details
```bash
curl -X GET "http://localhost:8080/api/v1/accounts/ACC001" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PaymentServiceTest

# Run with coverage
mvn clean test jacoco:report
```

## Performance Considerations

- **Database Indexing:** Strategic indexes on frequently queried columns
- **Caching:** Redis caching reduces database hits for account lookups
- **Query Optimization:** Custom JPQL queries with SELECT and FETCH strategies
- **Connection Pooling:** HikariCP for efficient database connection management
- **Batch Operations:** Bulk inserts and updates for transaction processing

## Scalability

- **Stateless Design:** Easy horizontal scaling
- **Distributed Caching:** Redis for cache sharing across instances
- **Read Replicas:** Database read-only replicas for report queries
- **Async Processing:** @EnableAsync for non-blocking operations
- **Circuit Breaker:** Resilience4j integration for external service calls

## Production Deployment

1. **Environment Configuration:**
```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/banking_db
export SPRING_REDIS_HOST=prod-redis
export JWT_SECRET=<secure-random-key>
```

2. **Monitoring & Logging:**
- Centralize logs with ELK/Splunk
- Monitor metrics with Prometheus/Grafana
- Set up alerts for critical events

3. **Security:**
- Enable HTTPS with SSL certificates
- Use environment variables for secrets
- Implement rate limiting
- Enable audit logging

## Contributing

Guidelines for contributing to the project:
1. Fork the repository
2. Create a feature branch
3. Commit changes with clear messages
4. Push to the branch
5. Submit a pull request

## License

MIT License - see LICENSE file for details

## Contact

**Banking Team**
- Email: banking@example.com
- Slack: #banking-team
