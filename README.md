# SKIT-CSE-2023-2027-05 — KronosTT

A monorepo college project with a Spring Boot backend and a React/TypeScript frontend.

---

## PROJECT ABSTRACT [Form–1] (YEAR–2026-27)

- **PROJECT ID:** SKIT/CSE/2023-2027/05
- **BRANCH:** Computer Science & Engineering
- **SECTION:** A
- **TITLE OF PROJECT:** KronosTT – AI-Powered Academic Scheduling Platform
- **SDG MAPPING:** SDG 4 – Quality Education
- **PROJECT TRACK:** R&D INNOVATION (✔)

### Problem Statement

Existing academic scheduling systems generate only an initial timetable and depend heavily on manual modification as institutions scale, offering limited intelligent optimization, poor conflict resolution, and no support for dynamic rescheduling or resource management. KronosTT proposes an AI-powered academic scheduling platform that uses constraint-based optimization (Google OR-Tools / CSP) and heuristic scheduling algorithms to automatically generate optimized, conflict-free academic timetables while efficiently managing faculty, classroom, and course constraints. The platform aims to minimize manual administrative effort, reduce scheduling conflicts, improve faculty and classroom utilization, and provide a scalable, modular architecture that can support dynamic rescheduling and future intelligent scheduling enhancements.

### Technology Stack

| Name of Tool / Technology | Frontend / Backend | Software / Hardware | Purpose of Use |
|---|---|---|---|
| Java 21 (LTS) | Backend | Software | Core application language for the backend |
| Spring Boot 3.x / Spring Data JPA / Hibernate | Backend | Software | REST API development, ORM and business logic |
| PostgreSQL | Backend | Software | Relational database for storing scheduling & institutional data |
| Google OR-Tools (CSP) | Backend / AI | Software | Constraint-based timetable optimization engine |
| Heuristic Scheduling Algorithms | Backend / AI | Software | Conflict detection & resolution in generated timetables |
| React.js + Tailwind CSS | Frontend | Software | Administrative dashboard and responsive UI |
| Spring Security + JWT | Backend | Software | Authentication & Role-Based Access Control (RBAC) |
| Redis | Backend | Software | Caching for performance optimization |
| Docker, Git & GitHub, Swagger/OpenAPI | Backend/DevOps | Software | Containerized deployment, version control, API documentation |

### Proposed Project Sprints

| S.No. | Sprint Name | Proposed User Story of Respective Sprint |
|---|---|---|
| 1 | Core Development & Security | As a developer, I want to finalise requirements and set up the database schema, faculty management, and secure authentication with RBAC and Google OAuth so that a secure foundation is ready for feature development. |
| 2 | Master Data & UI Design | As an admin, I want well-designed screens and room and subject management on an optimised database so that accurate master data is available for scheduling. |
| 3 | Scheduling Engine | As an admin, I want the system to auto-generate optimized, conflict-free timetables using constraint-based (OR-Tools CP-SAT) and heuristic algorithms so that manual scheduling effort is reduced. |
| 4 | DevOps & Integration | As a team, we want CI/CD build checks, a service layer separate from the engine, a Docker-based setup and the engine wired into the timetable service so that all parts work together reliably. |
| 5 | UI & Integration | As a user, I want an intuitive dashboard integrated with backend APIs so that I can generate, review, approve and adjust timetables and manage faculty, subjects and classrooms easily. |
| 6 | Engine Optimisation & Testing | As an admin, I want the engine to produce alternative schedules quickly and correctly at full load so that a whole department can be scheduled within the target time. |
| 7 | Timetable Services & Export | As a coordinator, I want CSV bulk import, background timetable generation jobs and PDF / Excel export so that data entry is fast and approved timetables can be shared. |
| 8 | Testing & Delivery | As a team, we want the system load-tested (JUnit test cases), deployed, documented and demo-ready so that it is ready for final evaluation and delivery. |

### Team Member Details

| Student Name | Mobile No. | Expertise Area | Role in Project |
|---|---|---|---|
| Bhavya Jain | 7073548870 | DevOps, Backend Services | Team Lead – DevOps & CI/CD, Service Layer, Engine Integration, Testing, Deployment, Debugging |
| Aaditya Jain | 8529088477 | AI/ML, Algorithms | Scheduling Engine Developer – AI/Constraint-based & Heuristic Algorithms + Engine Testing |
| Aman Sharma | 9216590177 | Backend, Security | Authentication & RBAC, Faculty Management (CRUD), Timetable Job & Export APIs |
| Bhavishya Raghav | 8302176131 | Backend, Frontend | Subject & Room Management, Administrative Dashboard, Landing Page |

---

## ROLES & RESPONSIBILITY OF TEAM MEMBERS [Form–2]

**PROJECT ID:** SKIT/CSE/2023-2027/05 | **BRANCH:** Computer Science & Engineering | **SECTION:** A  
**TITLE OF PROJECT:** KronosTT – AI-Powered Academic Scheduling Platform

### Team Lead: Bhavya Jain
**Sprint(s):** DevOps & Integration, Testing & Delivery

| User Story | Start Date | End Date | Details of Task Completed Under the User Story |
|---|---|---|---|
| Automated build checks | 10 Aug 2026 | 23 Aug 2026 | Set up GitHub CI/CD actions for backend and frontend |
| Dedicated service layer separated from engine | 24 Aug 2026 | 27 Sep 2026 | Implemented the service layer independent of the scheduling engine |
| JUnit Testing | 28 Sep 2026 | 18 Oct 2026 | Automated testing for the scheduling engine |
| Docker based build and deployment setup | 19 Oct 2026 | 01 Nov 2026 | Created Dockerfile, docker-compose and containerised backend |
| Engine integration with timetable service | 02 Nov 2026 | 06 Dec 2026 | Wired the generator and algorithms into the timetable service and stored the scheduled sessions |
| Performance and load testing | 07 Dec 2026 | 10 Jan 2027 | Full-load tests and performance profiling of timetable generation |
| Production deployment and monitoring | 11 Jan 2027 | 07 Feb 2027 | Automated deploy workflow, server setup and health monitoring |
| Final hardening, documentation and demo | 08 Feb 2027 | 15 Mar 2027 | Test coverage, bug fixes, project documentation and final demo |
| Debugging support for error free system runs | 10 Aug 2026 | 15 Mar 2027 | Debugging, error resolution and integration support across the platform |

### Member 1: Aaditya Jain
**Sprint(s):** Scheduling Engine, Engine Optimisation & Testing

| User Story | Start Date | End Date | Details of Task Completed Under the User Story |
|---|---|---|---|
| Research for the constraint based algorithm implementation | 10 Aug 2026 | 23 Aug 2026 | Researched the constraint satisfaction problem, Google OR-Tools and how they fit the project's use case |
| Constraint based algorithm development | 24 Aug 2026 | 27 Sep 2026 | Designed and implemented the core constraint-satisfaction logic |
| Session generator | 28 Sep 2026 | 18 Oct 2026 | Generated schedulable sessions from subjects, batches and credit splits |
| OR-Tools integration with the algorithm | 19 Oct 2026 | 01 Nov 2026 | Implemented and integrated the constraint model using Google OR-Tools' CP-SAT solver |
| Heuristic based conflict resolution | 02 Nov 2026 | 22 Nov 2026 | Implemented a heuristic based scheduling algorithm for conflict detection and resolution |
| Constraint-propagation and multiple-schedule algorithms | 23 Nov 2026 | 27 Dec 2026 | Implemented constraint-propagation placement and generation of multiple alternative schedules |
| Engine testing and full-load benchmarks | 28 Dec 2026 | 24 Jan 2027 | Unit and full-load tests to check engine correctness and scale |
| Engine performance tuning and algorithm selection | 25 Jan 2027 | 21 Feb 2027 | Optimised generation time and picked the best algorithm per input size |
| Engine bug fixes and documentation | 22 Feb 2027 | 15 Mar 2027 | Fixed engine issues and documented the engine design |

### Member 2: Aman Sharma
**Sprint(s):** Core Development & Security, Timetable Services & Export

| User Story | Start Date | End Date | Details of Task Completed Under the User Story |
|---|---|---|---|
| Research and requirement analysis | 10 Aug 2026 | 20 Sep 2026 | Studied auth, RBAC and multi-tenant approaches and finalised requirements |
| Research and development for database schema | 21 Sep 2026 | 04 Oct 2026 | Database implementation in PostgreSQL |
| CRUD operations on faculty | 05 Oct 2026 | 18 Oct 2026 | Built the faculty management module |
| Secure authentication and authorization | 19 Oct 2026 | 08 Nov 2026 | Implemented authentication using Spring Security with JWT |
| RBAC system implementation and multi-tenant architecture | 09 Nov 2026 | 29 Nov 2026 | Implemented multi-level role authorization and permission admin control |
| Google OAuth integration for convenient auth | 30 Nov 2026 | 20 Dec 2026 | Integrated OAuth2 with the login system and adapted it to the custom RBAC system |
| CSV bulk import of master data | 21 Dec 2026 | 03 Jan 2027 | Bulk upload of subjects, rooms, faculty and batches with validation |
| Timetable job API | 04 Jan 2027 | 31 Jan 2027 | Async timetable generation jobs with a status-checking API |
| PDF / Excel export API | 01 Feb 2027 | 21 Feb 2027 | Export of approved timetables to PDF and Excel |
| Integration testing and bug fixes | 22 Feb 2027 | 15 Mar 2027 | End-to-end API tests and bug fixes |

### Member 3: Bhavishya Raghav
**Sprint(s):** Master Data & UI Design, UI & Integration

| User Story | Start Date | End Date | Details of Task Completed Under the User Story |
|---|---|---|---|
| Research and UI/UX design | 10 Aug 2026 | 20 Sep 2026 | Studied user workflows and designed wireframes for all screens |
| Optimise the database with indexes for better performance | 21 Sep 2026 | 04 Oct 2026 | Optimised and updated the database schema for faster service performance |
| CRUD operations on rooms | 05 Oct 2026 | 18 Oct 2026 | Designed and built the room management module |
| CRUD operations on subjects | 19 Oct 2026 | 01 Nov 2026 | Designed and built the subject management module |
| Login pages | 02 Nov 2026 | 15 Nov 2026 | Designed and built the login pages |
| Landing page | 16 Nov 2026 | 29 Nov 2026 | Designed and built the landing page |
| CRUD based pages for subjects, rooms and faculties | 30 Nov 2026 | 13 Dec 2026 | Designed and built CRUD pages for subjects, rooms and faculties |
| Timetable generation page (full overview of inputs) | 14 Dec 2026 | 27 Dec 2026 | Designed and built the timetable generation page |
| Timetable review + approve/reject page | 28 Dec 2026 | 10 Jan 2027 | Designed and built the review page |
| Timetable views and manual lock/move | 11 Jan 2027 | 07 Feb 2027 | Batch, teacher and room views with drag-to-move and session locking |
| Responsive UI polish and usability testing | 08 Feb 2027 | 15 Mar 2027 | Made the UI responsive and fixed issues found in usability testing |

---

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