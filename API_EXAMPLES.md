# API Examples

This file contains practical examples of using the Banking Payment Service API.

## Prerequisites

1. Get a JWT token:
```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "email": "user@example.com"
  }' | jq -r '.data.token'
```

Save the token as: `TOKEN=<your-jwt-token>`

## Authentication API

### Login
```bash
curl -X POST "http://localhost:8080/api/v1/auth/login?userId=user123&email=user@example.com"
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "user123",
    "email": "user@example.com",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer"
  },
  "timestamp": 1626912345000
}
```

### Validate Token
```bash
curl -X POST "http://localhost:8080/api/v1/auth/validate?token=$TOKEN"
```

## Account Management API

### List User Accounts
```bash
curl -X GET "http://localhost:8080/api/v1/accounts" \
  -H "Authorization: Bearer $TOKEN"
```

Response:
```json
{
  "success": true,
  "message": "Accounts retrieved successfully",
  "data": [
    {
      "id": 1,
      "account_number": "ACC001",
      "account_holder": "John Doe",
      "balance": 5000.00,
      "status": "ACTIVE",
      "account_type": "CHECKING",
      "created_at": "2024-01-15T10:30:00",
      "updated_at": "2024-01-15T10:30:00"
    }
  ],
  "timestamp": 1626912345000
}
```

### Get Account Details
```bash
curl -X GET "http://localhost:8080/api/v1/accounts/ACC001" \
  -H "Authorization: Bearer $TOKEN"
```

### Create New Account
```bash
curl -X POST "http://localhost:8080/api/v1/accounts?accountNumber=ACC003&accountHolder=Mike%20Johnson&accountType=SAVINGS" \
  -H "Authorization: Bearer $TOKEN"
```

### Deactivate Account
```bash
curl -X PUT "http://localhost:8080/api/v1/accounts/ACC001/deactivate" \
  -H "Authorization: Bearer $TOKEN"
```

## Payment Processing API

### Process Payment
```bash
curl -X POST "http://localhost:8080/api/v1/payments" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "from_account_number": "ACC001",
    "to_account_number": "ACC002",
    "amount": 250.50,
    "payment_type": "TRANSFER",
    "description": "Monthly rent payment"
  }'
```

Response:
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "data": {
    "id": 1,
    "reference_number": "PAY-1626912345000-A1B2C3D4",
    "from_account_number": "ACC001",
    "to_account_number": "ACC002",
    "amount": 250.50,
    "status": "COMPLETED",
    "payment_type": "TRANSFER",
    "description": "Monthly rent payment",
    "created_at": "2024-01-15T11:30:00",
    "updated_at": "2024-01-15T11:30:00"
  },
  "timestamp": 1626912345000
}
```

### Get Payment Details
```bash
curl -X GET "http://localhost:8080/api/v1/payments/PAY-1626912345000-A1B2C3D4" \
  -H "Authorization: Bearer $TOKEN"
```

### Get Account Payment History
```bash
curl -X GET "http://localhost:8080/api/v1/payments/account/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Get Pending Payments
```bash
curl -X GET "http://localhost:8080/api/v1/payments/pending" \
  -H "Authorization: Bearer $TOKEN"
```

## Transaction History API

### Get Account Transactions
```bash
curl -X GET "http://localhost:8080/api/v1/transactions/account/1" \
  -H "Authorization: Bearer $TOKEN"
```

Response:
```json
{
  "success": true,
  "message": "Transactions retrieved successfully",
  "data": [
    {
      "id": 1,
      "account_number": "ACC001",
      "amount": -250.50,
      "type": "TRANSFER_OUT",
      "description": "Transfer to ACC002",
      "balance_after": 4749.50,
      "transaction_date": "2024-01-15T11:30:00"
    },
    {
      "id": 2,
      "account_number": "ACC001",
      "amount": -50.00,
      "type": "FEE",
      "description": "Monthly maintenance fee",
      "balance_after": 4699.50,
      "transaction_date": "2024-01-15T12:00:00"
    }
  ],
  "timestamp": 1626912345000
}
```

### Get Transactions by Date Range
```bash
curl -X GET "http://localhost:8080/api/v1/transactions/account/1/range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59" \
  -H "Authorization: Bearer $TOKEN"
```

## Health & Monitoring

### Application Health
```bash
curl -X GET "http://localhost:8080/api/v1/health"
```

### Actuator Metrics
```bash
curl -X GET "http://localhost:8080/actuator"
curl -X GET "http://localhost:8080/actuator/metrics"
curl -X GET "http://localhost:8080/actuator/health"
```

## Error Handling Examples

### Insufficient Funds
```bash
curl -X POST "http://localhost:8080/api/v1/payments" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "from_account_number": "ACC001",
    "to_account_number": "ACC002",
    "amount": 999999.99,
    "payment_type": "TRANSFER"
  }'
```

Response:
```json
{
  "success": false,
  "message": "Payment processing failed: Insufficient funds",
  "data": null,
  "timestamp": 1626912345000
}
```

### Invalid Account
```bash
curl -X GET "http://localhost:8080/api/v1/accounts/INVALID" \
  -H "Authorization: Bearer $TOKEN"
```

Response:
```json
{
  "success": false,
  "message": "Payment processing failed: Account not found: INVALID",
  "data": null,
  "timestamp": 1626912345000
}
```

### Validation Error
```bash
curl -X POST "http://localhost:8080/api/v1/payments" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "from_account_number": "ACC001",
    "to_account_number": "ACC002",
    "amount": -100,
    "payment_type": "TRANSFER"
  }'
```

Response:
```json
{
  "error": "Validation Error",
  "message": "Input validation failed: {amount=Amount must be greater than 0}",
  "status": 400,
  "timestamp": 1626912345000
}
```

## Testing with Postman

1. Create a Postman environment with:
   - `base_url`: http://localhost:8080
   - `token`: <your-jwt-token>

2. Use these requests:
   - `{{base_url}}/api/v1/auth/login`
   - `{{base_url}}/api/v1/accounts`
   - `{{base_url}}/api/v1/payments`
   - `{{base_url}}/api/v1/transactions/account/1`

3. In request headers, add:
   - `Authorization: Bearer {{token}}`
   - `Content-Type: application/json`

## Testing with httpie

```bash
# Install httpie
pip install httpie

# Login and get token
http POST localhost:8080/api/v1/auth/login userId=user123 email=user@example.com

# Set token
export TOKEN="<your-token>"

# Get accounts
http GET localhost:8080/api/v1/accounts Authorization:"Bearer $TOKEN"

# Create payment
http POST localhost:8080/api/v1/payments \
  Authorization:"Bearer $TOKEN" \
  from_account_number=ACC001 \
  to_account_number=ACC002 \
  amount:=250.50
```

## Load Testing with Apache JMeter

Create a test plan:
1. Login endpoint
2. Create account endpoint
3. Process payment endpoint
4. Get transactions endpoint

Run with:
```bash
jmeter -n -t test_plan.jmx -l results.jtl -j log.txt
```

---

**API Examples Information**
- **Created**: January 2024
- **Last Updated**: 2024-01-15
- **API Version**: 1.0
- **Examples Valid For**: Spring Boot 3.3.0+
