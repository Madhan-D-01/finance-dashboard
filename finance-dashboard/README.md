# Finance Dashboard Backend

A **Spring Boot-based backend system** for managing financial records with **role-based access control**, **data processing**, and **dashboard analytics**.

This project demonstrates backend design principles including **API development, security, data modeling, and business logic implementation**.

---

# Features

## User & Role Management
- Create and manage users
- Assign roles: **ADMIN, ANALYST, VIEWER**
- User status management (**ACTIVE / INACTIVE**)
- Role-based access enforcement using Spring Security

---

## Financial Records Management
- Create, update, delete financial records
- Fields include:
  - Amount
  - Type (**INCOME / EXPENSE**)
  - Category
  - Date
  - Notes
- Link records to users
- Pagination support
- Filtering:
  - By type
  - By category
  - By date range

---

## Dashboard Analytics APIs
- Total income
- Total expenses
- Net balance
- Category-wise totals
- Recent activity
- Monthly trends

---

## Security & Access Control
Implemented using **Spring Security**:

### Roles & Permissions

| Role    | Permissions |
|--------|------------|
| VIEWER | View records |
| ANALYST | View records + dashboard analytics |
| ADMIN  | Full access (users + records + analytics) |

### Security Features
- HTTP Basic Authentication
- Password encryption using BCrypt
- Role-based authorization using `@PreAuthorize`
- Endpoint-level access control via `SecurityFilterChain`
- Inactive users are blocked from login

---

## Validation & Error Handling
- Input validation using Jakarta Validation (`@NotNull`, `@NotBlank`, etc.)
- Global exception handling using `@ControllerAdvice`
- Custom exceptions:
  - `ResourceNotFoundException`
- Proper HTTP status codes:
  - `404 Not Found`
  - `403 Forbidden`
  - `500 Internal Server Error`

---

## Data Persistence
- MySQL database
- JPA (Hibernate) for ORM
- Automatic schema update (`ddl-auto=update`)

---

## Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Security**
- **Spring Data JPA**
- **MySQL**
- **Swagger (OpenAPI)**
- **Lombok**

---

# Project Structure

```
finance-dashboard/
│
├── controller/        # REST Controllers
├── service/           # Business logic
├── repository/        # Data access layer
├── model/             # Entities & enums
├── dto/               # Request/Response objects
├── security/          # Security configuration
├── exception/         # Exception handling
└── FinanceDashboardApplication.java
```

---

# Setup & Installation

## Clone Repository
```
git clone https://github.com/Madhan-D-01/finance-dashboard.git
cd finance-dashboard
```

---

## Configure Database

Update `application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

---

## Create Database

```
CREATE DATABASE finance_db;
```

---

## Run Application

```
mvn spring-boot:run
```

Application will start at:
```
http://localhost:8080
```

---

## Default Admin User

Automatically created on startup:

```
Email: admin@example.com
Password: admin123
```

---

# API Documentation (Swagger)

Access Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---
# Sample Request Payloads

These examples can be used directly in Postman or Swagger for testing APIs.

### Create User (ADMIN)

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "ANALYST",
  "status": "ACTIVE"
}
```

---

### Create Record (ADMIN)

```json
{
  "amount": 67000,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-08",
  "notes": "April salary",
  "userId": 1
}
```

---

### Filter Records Example

```
GET /api/records/filter?type=INCOME&category=Salary&startDate=2026-04-01&endDate=2026-04-30
```

---

# API Endpoints

## Users (ADMIN only)

| Method | Endpoint | Description |
|--------|--------|------------|
| POST | /api/users | Create user |
| GET | /api/users | Get all users |
| PUT | /api/users/{id} | Update user |

---

## Records

| Method | Endpoint | Access | Description |
|--------|--------|--------|------------|
| POST | /api/records | ADMIN | Create record |
| GET | /api/records | ALL | Get records (paginated) |
| PUT | /api/records/{id} | ADMIN | Update record |
| DELETE | /api/records/{id} | ADMIN | Delete record |
| GET | /api/records/filter | ALL | Filter records |

---

## Dashboard

| Endpoint | Access | Description |
|----------|--------|------------|
| /api/dashboard/summary | ALL | Income, expense, balance |
| /api/dashboard/category-summary | ALL | Category totals |
| /api/dashboard/recent | ALL | Recent records |
| /api/dashboard/monthly-trends | ADMIN, ANALYST | Monthly analysis |

---

# Design Decisions

- Layered Architecture: Controller → Service → Repository
- DTO Usage: Prevents exposing internal entities
- Enums: Used for role, status, and record type to ensure data consistency
- Service Layer Logic: Handles business rules and validation
- Security at Multiple Levels:
  - Endpoint-level
  - Method-level (@PreAuthorize)

---

# Assumptions

- Each record belongs to one user
- Roles are fixed: ADMIN, ANALYST, VIEWER
- Aggregation is performed in memory (for simplicity)
- Authentication is handled using HTTP Basic (not JWT)

---

# Future Improvements

- JWT-based authentication
- Database-level aggregation queries (for performance)
- Soft delete for records
- Pagination for dashboard APIs
- Unit & integration testing
- Docker deployment
- Role-based UI integration

---

# Conclusion

This project demonstrates a well-structured backend system with:

- Clean architecture  
- Secure role-based access control  
- Efficient data handling  
- Real-world API design  

It reflects strong backend engineering fundamentals and practical implementation skills.

---

# Author
Madhan
