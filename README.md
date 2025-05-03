
## Overview

CashCard is a secure and user-friendly service designed to manage family cash cards. This system enables family members to create, manage, and track their cash cards, providing a modern for financial management within families. 

## Key Features

### API Endpoints
- 🔐 Secure authentication and authorization 
- 💳 Cash card management

### Technical Highlights
- 🔄 RESTful API design
- 🧪 Test Driven Development: Testing First
- 📦 JUnit Testing 
- 🔐 Spring Security with Basic Auth
  
## Technical Stack

### Backend
- **Framework**: Spring Boot 3.3.10
- **Language**: Java 17
- **Database**: H2 (in-memory)
- **Security**: Spring Security
- **Validation**: Bean Validation
- **ORM**: Spring Data JPA
- **Dependency Injection**: Spring IoC

### Key Dependencies
```xml
- Spring Boot Web
- Spring Data JPA
- H2 Database
- Spring Security
- OpenAPI/Swagger
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6.0 or higher

### Installation
1. Clone the repository
```bash
git clone https://github.com/AlexisTuzPerez/cashcard
cd cashcard
```

2. Build and run the application
```bash
./mvnw clean install
./mvnw spring-boot:run
```

## Project Structure

```
cashcard/
├── src/main/java/com/example/cashcard/
│   ├── cashcard/         # CashCard entity and controller
│   ├── config/           # Application configuration
│   └── CashcardApplication.java
├── pom.xml
└── README.md
```

## API Documentation

### Base URL

http://localhost:8080

### Swagger UI

Access the interactive API documentation at:

http://localhost:8080/swagger-ui/index.html


## License

This project is proprietary and confidential. All rights reserved.
