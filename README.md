# Finance Dashboard

A full-stack finance tracking application with role-based access control, dashboard analytics, and a REST API backend. Built to demonstrate production-grade backend design, secure authentication, and a polished frontend on top of it.

**Live demo**
- Frontend: https://finsight012.netlify.app
- Backend API docs (Swagger): https://finance-dashboard-backend-a97w.onrender.com/swagger-ui/index.html

Note: the backend runs on a free-tier host and goes to sleep after periods of inactivity. The first request after a period of inactivity may take 30-60 seconds while it wakes up.

---

## Overview

The application lets users record income and expenses, categorize them, and view dashboard analytics (totals, category breakdowns, monthly trends). Access is controlled through three roles:

- **Admin** — full access: manage users, create/edit/delete records, view all analytics
- **Analyst** — view records and dashboard analytics, no write access
- **Viewer** — view records only

## Tech stack

**Backend**
- Java 17
- Spring Boot 3
- Spring Security with JWT authentication
- Spring Data JPA / Hibernate
- MySQL
- Swagger / OpenAPI documentation
- JUnit 5, Mockito, AssertJ for testing

**Frontend**
- React 18
- Vite
- Tailwind CSS
- Recharts
- React Router
- Axios

**Infrastructure**
- Docker and Docker Compose for local full-stack deployment
- Jenkins for CI/CD (build, test, containerize, deploy)
- Render (backend hosting)
- Netlify (frontend hosting)
- Aiven (managed MySQL)

## Features

- JWT-based authentication with role-based authorization enforced at both the endpoint and method level
- Financial record management: create, update, delete, filter by type/category/date range, with pagination
- Dashboard analytics computed at the database level (SQL aggregation, not in-memory iteration): total income, total expense, net balance, category totals, monthly trends, recent activity
- User management for administrators, including role and status control
- INR currency formatting throughout
- Centralized exception handling with consistent HTTP status codes
- Input validation on all write endpoints

## Project structure

```
.
├── finance-dashboard/        Backend (Spring Boot)
│   ├── src/main/java/...
│   ├── src/test/java/...
│   ├── Dockerfile
│   └── pom.xml
├── finance-ui/                Frontend (React + Vite)
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
└── Jenkinsfile
```

## Getting started locally

### Prerequisites

- Java 17
- Maven
- Node.js and npm
- MySQL (or Docker, see below)

### Backend

1. Create a MySQL database:
   ```sql
   CREATE DATABASE finance_db;
   ```
2. Set the following environment variables before running:
   ```
   DB_URL=jdbc:mysql://localhost:3306/finance_db
   DB_USERNAME=root
   DB_PASSWORD=your_password
   JWT_SECRET=a_long_random_secret
   ADMIN_EMAIL=admin@example.com
   ADMIN_PASSWORD=choose_a_password
   ```
3. Run:
   ```
   cd finance-dashboard
   mvn spring-boot:run
   ```
4. The API is available at `http://localhost:8082`. Swagger UI is at `http://localhost:8082/swagger-ui/index.html`.

### Frontend

1. ```
   cd finance-ui
   npm install
   cp .env.example .env
   ```
2. Edit `.env` if the backend isn't running on the default `http://localhost:8082`.
3. ```
   npm run dev
   ```
4. The app runs at `http://localhost:5500`.

### Running the full stack with Docker Compose

From the repository root, with a `.env` file defining `DB_PASSWORD`, `JWT_SECRET`, `ADMIN_EMAIL`, and `ADMIN_PASSWORD`:

```
docker compose up --build
```

This starts MySQL, the backend, and the frontend together. The frontend is served at `http://localhost:5500`, the backend at `http://localhost:8082`.

## Running tests

```
cd finance-dashboard
mvn clean test
```

Tests run against an in-memory H2 database, so no live MySQL connection is required.

## CI/CD

A Jenkins pipeline is defined in `Jenkinsfile`. On each run it:

1. Checks out the latest code
2. Builds and tests the backend (`mvn clean verify`)
3. Installs dependencies and builds the frontend
4. Builds Docker images for both services
5. Redeploys the stack via Docker Compose

## API summary

| Area | Endpoint | Access |
|---|---|---|
| Auth | `POST /api/auth/login` | Public |
| Users | `POST /api/users`, `GET /api/users`, `PUT /api/users/{id}` | Admin |
| Records | `POST /api/records`, `PUT /api/records/{id}`, `DELETE /api/records/{id}` | Admin |
| Records | `GET /api/records`, `GET /api/records/filter` | Admin, Analyst, Viewer |
| Dashboard | `GET /api/dashboard/summary`, `/category-summary`, `/recent`, `/monthly-trends` | Admin, Analyst |

Full request and response schemas are available through Swagger UI at runtime.

## Security notes

- Passwords are hashed with BCrypt
- Authentication is stateless, using signed JWTs
- All secrets (database credentials, JWT signing key, admin credentials) are supplied through environment variables and are not committed to the repository
- CORS is restricted to explicitly allowed origins

## License

Not currently licensed for redistribution. Contact the author for usage terms.

## Author

Madhan
