# Eagle Bank API

A REST API for Eagle Bank that provides basic banking operations including user management and bank account management.

## Project Overview

Eagle Bank API is a Spring Boot application that allows users to create and manage bank accounts. This is a test project created by Mazen Srari that demonstrates modern Spring Boot development practices with comprehensive validation, testing, and API documentation.

**Current Status**: This implementation includes user and bank account creation/retrieval functionality. Transaction APIs are not yet implemented.

## Features

### Implemented Features
- **User Management**
  - Create new users 
  - Fetch user details by ID
  
- **Bank Account Management**
  - Create new bank accounts linked to users
  - Fetch bank account details by account number

### Planned Features (Not Yet Implemented)
- Account listing
- Account updates and deletion
- Transaction management (deposits, withdrawals)
- Transaction history
- Authentication & authorization

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database for development
- **SpringDoc OpenAPI 3** - API documentation
- **Lombok** - Boilerplate code reduction
- **Maven** - Dependency management

### Base URL
```
http://localhost:8080/api
```

### Available Endpoints

#### Users
- `POST /v1/users` - Create a new user
- `GET /v1/users/{userId}` - Fetch user by ID (format: `usr-[A-Za-z0-9]+`)

#### Bank Accounts
- `POST /v1/accounts` - Create a new bank account
- `GET /v1/accounts/{accountNumber}` - Fetch account by account number (format: `01XXXXXX`)

### API Documentation URLs
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd eagle-bank-api
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Base URL: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - H2 Console: http://localhost:8080/api/h2-console

### Running Tests
```bash
mvn clean test
```

## Database Configuration

The application uses H2 in-memory database for development:

- **JDBC URL**: `jdbc:h2:mem:eaglebank`
- **Console**: http://localhost:8080/api/h2-console
- **Username**: Default H2 credentials
- **DDL Strategy**: `create-drop` (recreates schema on each startup)

## Data Models

### User Entity
- **ID**: Auto-generated primary key
- **Name**: User's full name
- **Email**: User's email address
- **Phone Number**: Contact number
- **Address**: Physical address
- **Timestamps**: Created and updated timestamps

### Bank Account Entity
- **Account Number**: Unique 8-digit number (format: 01XXXXXX)
- **Sort Code**: Bank sort code
- **Name**: Account name
- **Account Type**: Type of account (e.g., PERSONAL)
- **Balance**: Account balance (default: £0.0)
- **Currency**: Account currency
- **User**: Link to account owner
- **Timestamps**: Created and updated timestamps

## Configuration

### Application Properties
```yaml
server.port: 8080
server.servlet.context-path: /api
spring.application.name: eagle-bank-api
```

### Monitoring
Health checks and metrics are available via Spring Boot Actuator:
- Health: http://localhost:8080/api/actuator/health
- Info: http://localhost:8080/api/actuator/info
- Metrics: http://localhost:8080/api/actuator/metrics

## Testing

The project includes comprehensive test coverage:

- **Controller Tests**: API endpoint testing
- **Service Tests**: Business logic testing
- **Validation Tests**: Input validation testing
- **Repository Tests**: Data access testing

Test reports are generated in `target/surefire-reports/`

In a real world scenario, the project would include integration tests to verify end-to-end functionality.

## Development

### Code Quality
- Lombok annotations for reducing boilerplate
- Comprehensive validation using Jakarta Validation
- Custom exception handling
- Structured logging with configurable levels

### Project Structure
```
src/
├── main/java/com/eaglebank/eagle_bank_api/
│   ├── controller/     # REST controllers
│   ├── service/        # Business logic
│   ├── repository/     # Data access layer
│   ├── model/          # JPA entities
│   └── exception/      # Custom exceptions
├── main/resources/
│   ├── application.yaml
│   └── openapi.yaml    # API specification
└── test/               # Test classes
```

## Author

**Mazen Srari** - Eagle Bank API Test Project

## License

This project is created for demonstration purposes.
