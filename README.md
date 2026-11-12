# SKIT-CSE-2023-2027-05

A monorepo college project with a Spring Boot backend and a React/TypeScript frontend, currently in scaffolding stage.

## Getting started

### Run everything with Docker

Needs Docker with Compose. Starts PostgreSQL, the backend and the frontend:

```bash
cp .env.example .env   # then set POSTGRES_PASSWORD
docker compose up --build
```

The app runs on `http://localhost:3000` (change with `APP_PORT`). nginx serves the
frontend and forwards `/api` to the backend, which is not exposed on its own.
Data lives in the `db-data` volume; `docker compose down -v` wipes it.

### Backend setup

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs on `http://localhost:8080` and needs Java 25 and PostgreSQL.

Tests need a PostgreSQL database `backend_test` on `localhost:5432` (user and password
`postgres`); every run rebuilds its schema from the Flyway migrations. For a throwaway one:
```bash
docker run -d --name kronos-test-pg -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=backend_test postgres:18
./mvnw test
```

### Frontend setup

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:5173` (Vite dev server).

Build for production:
```bash
npm run build
```

Run tests:
```bash
npm test
```

<!-- CI verification commit -->