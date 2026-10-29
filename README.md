# SKIT-CSE-2023-2027-05

A monorepo college project with a Spring Boot backend and a React/TypeScript frontend, currently in scaffolding stage.

## Getting started

### Backend setup

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs on `http://localhost:8080`. Uses PostgreSQL in production, H2 in-memory for tests.

Run tests:
```bash
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