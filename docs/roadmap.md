# Roadmap — KronosTT (SKIT-CSE-2023-2027-05)

31-week plan for a team of 4, from 2026-08-31 to 2027-03-31. The original
12-week plan (ending 2026-11-22) was extended with mentor approval. Weeks
run Monday to Sunday.

Scheduling rules:
- Every task takes at least 1 week and at most 5 weeks.
- Every member has at least one task in every week of the project.
- Within a member's track, tasks run back to back. The only overlap is
  Bhavya's full-time debugging and integration support.

This file matches the Roles & Responsibility form (Form-2).

## Team tracks

| Track | Owner | Scope |
|---|---|---|
| Platform / integration | Bhavya Jain (lead) | Infra, CI/CD, service layer, engine integration, testing, deployment, reviews |
| Engine | Aaditya Jain | `com.kronos.engine`: TimeTableState, constraints, session generator, placement algorithms |
| Core backend | Aman Sharma | Schema, faculty CRUD, auth (JWT, RBAC, OAuth2), import, job and export APIs |
| UI + data | Bhavishya Raghav | DB indexing, room and subject CRUD, all frontend screens |

`kronostt-ref` is the reference implementation. **Its engine
(`engine/SessionGenerator`, `engine/algorithm/*`, `engine/constraints/*`,
`TimeTableState`) is out of scope for everyone except the engine track.**
Everything else in it (services, mappers, DTOs, controllers,
`TimetableJobEntity`, Docker/deploy) is fair to port and improve.

## Status snapshot (as of 2026-10-14)

**Done on `main`**
- Monorepo scaffold, backend + frontend CI (path-filtered, PR-gated)
- JPA entities + repositories for Subject, Teacher, Batch, Room, Session, ScheduledSession
- Swagger, CORS, `/health` endpoint + frontend connection check
- Flyway migrations, `ddl-auto=validate`; `subject_type` stored as a string
- Generic `audit_log` via JPA entity listener on `BaseEntity`
- Engine models + `Constraints` interface + `TimeTableState`
- `BatchService`, `BatchMapper` (MapStruct) + `BatchDto`, `AbstractPlacementAlgo`
- API error handling: `GlobalExceptionHandler` + `ApiError` (404 / 400 with field errors / 409 / 500), `ResourceNotFoundException`, `ConflictException`, shared `CentralMapperConfig`; `-parameters` compiler flag
- Weekly Form-3 report automation

**In progress**
- Bhavya: service layer foundation (`GlobalExceptionHandler`, `ApiError`, entity fixes) — until 2026-10-18
- Aaditya: concrete constraints — until 2026-10-18
- Aman: database schema work — until 2026-10-25
- Bhavishya: database indexing — until 2026-10-25

## Schedule

Status: ✅ done · 🔄 in progress · ⏳ planned

### Bhavya Jain — Research, Integration, Testing and Delivery

| Task | Start | End | Status |
|---|---|---|---|
| Automated build checks (GitHub CI/CD) | 2026-08-31 | 2026-09-13 | ✅ |
| Dedicated service layer separated from engine | 2026-09-14 | 2026-10-18 | ✅ |
| JUnit testing | 2026-10-19 | 2026-11-08 | 🔄 |
| Docker based build and deployment setup | 2026-11-09 | 2026-11-22 | ⏳ |
| Engine integration with timetable service | 2026-11-23 | 2026-12-27 | ⏳ |
| Performance and load testing | 2026-12-28 | 2027-01-31 | ⏳ |
| Production deployment and monitoring | 2027-02-01 | 2027-02-28 | ⏳ |
| Final hardening, documentation and demo | 2027-03-01 | 2027-03-31 | ⏳ |
| Debugging and integration support (full time) | 2026-08-31 | 2027-03-31 | 🔄 |

### Aaditya Jain — Research, Scheduling Engine

| Task | Start | End | Status |
|---|---|---|---|
| Research on constraint-based algorithms (CSP, OR-Tools) | 2026-08-31 | 2026-09-13 | ✅ |
| Constraint-based algorithm development | 2026-09-14 | 2026-10-18 | 🔄 |
| Session generator | 2026-10-19 | 2026-11-08 | ⏳ |
| OR-Tools CP-SAT integration | 2026-11-09 | 2026-11-22 | ⏳ |
| Heuristic based conflict resolution | 2026-11-23 | 2026-12-13 | ⏳ |
| Constraint-propagation and multiple-schedule algorithms | 2026-12-14 | 2027-01-17 | ⏳ |
| Engine testing and full-load benchmarks | 2027-01-18 | 2027-02-14 | ⏳ |
| Engine performance tuning and algorithm selection | 2027-02-15 | 2027-03-14 | ⏳ |
| Engine bug fixes and documentation | 2027-03-15 | 2027-03-31 | ⏳ |

Engine work remaining (vs. reference): 4 concrete constraints (teacher
unavailability, daily load, subject spacing, preferred slot),
`SessionGenerator`, `BaseAlgo` / `HeuristicPlacementAlgo` /
`ConstraintPropagationAlgo` / `MultipleSchedulesAlgo`, and their tests.

### Aman Sharma — Research, Core Development

| Task | Start | End | Status |
|---|---|---|---|
| Research and requirement analysis | 2026-08-31 | 2026-10-11 | ✅ |
| Database schema development | 2026-10-12 | 2026-10-25 | 🔄 |
| CRUD API: faculty (teachers) | 2026-10-26 | 2026-11-08 | ⏳ |
| Authentication and authorization (Spring Security + JWT) | 2026-11-09 | 2026-11-29 | ⏳ |
| RBAC + multi-tenant architecture | 2026-11-30 | 2026-12-20 | ⏳ |
| Google OAuth2 integration | 2026-12-21 | 2027-01-10 | ⏳ |
| CSV bulk import of master data | 2027-01-11 | 2027-01-24 | ⏳ |
| Timetable job API (async generation + status polling) | 2027-01-25 | 2027-02-21 | ⏳ |
| PDF / Excel export API | 2027-02-22 | 2027-03-14 | ⏳ |
| Integration testing and bug fixes | 2027-03-15 | 2027-03-31 | ⏳ |

### Bhavishya Raghav — Research, UI and Integration

| Task | Start | End | Status |
|---|---|---|---|
| Research and UI/UX design | 2026-08-31 | 2026-10-11 | ✅ |
| Optimise the database with indexes | 2026-10-12 | 2026-10-25 | 🔄 |
| CRUD API: rooms | 2026-10-26 | 2026-11-08 | ⏳ |
| CRUD API: subjects | 2026-11-09 | 2026-11-22 | ⏳ |
| Login pages | 2026-11-23 | 2026-12-06 | ⏳ |
| Landing page | 2026-12-07 | 2026-12-20 | ⏳ |
| CRUD pages for subjects, rooms and faculty | 2026-12-21 | 2027-01-03 | ⏳ |
| Timetable generation page (full overview of inputs) | 2027-01-04 | 2027-01-17 | ⏳ |
| Timetable review + approve/reject page | 2027-01-18 | 2027-01-31 | ⏳ |
| Timetable views (batch / teacher / room) + manual lock/move | 2027-02-01 | 2027-02-28 | ⏳ |
| Responsive UI polish and usability testing | 2027-03-01 | 2027-03-31 | ⏳ |

## Milestones

| By | Milestone |
|---|---|
| 2026-10-18 | API error handling foundation; concrete constraints |
| 2026-11-22 | Master-data CRUD APIs (faculty, rooms, subjects); session generator; JWT auth; Docker build |
| 2026-12-27 | Engine wired into `TimetableService` (synchronous generation); RBAC; login and landing pages |
| 2027-01-31 | All master-data and timetable generation/review screens; OAuth2; CSV import |
| 2027-02-28 | Async job API; timetable views with lock/move; production deployment |
| 2027-03-31 | Export, performance tuning, hardening, documentation, final demo |

## Cross-track dependencies

- **Auth before login UI:** JWT (Aman, ends 2026-11-29) comes before the
  login pages (Bhavishya, starting 2026-11-23). The first week of login
  page work can use a stubbed token.
- **CRUD APIs before CRUD pages:** the faculty, room and subject APIs end
  by 2026-11-22. The CRUD pages start 2026-12-21.
- **Engine integration before the generation page:** Bhavya's integration
  (ends 2026-12-27) exposes a **synchronous** generate endpoint. The
  generation and review pages (2027-01) build against it.
- **Async job API after integration:** Aman's job API (2027-01-25 to
  2027-02-21) wraps that same service call in a background job. The
  generation page switches to status polling in February, alongside the
  timetable views work.
- **Engine contract:** the `TimetableService` ↔ engine interface must be
  agreed before 2026-11-23, so engine algorithm work (Dec–Jan) and
  integration can go ahead independently.

## Master-data CRUD API spec

Full REST CRUD for the four master-data resources, under
`/api/v1/{subjects,rooms,teachers,batches}`:

| Method | Path | Result |
|---|---|---|
| GET | `/` | Paged list (`?page=&size=&sort=`) |
| GET | `/{id}` | Single resource, 404 if missing |
| POST | `/` | 201 + created resource |
| PUT | `/{id}` | 200 + updated resource |
| DELETE | `/{id}` | 204; 409 if the row is still referenced |

Layers per resource: DTO (with Bean Validation), MapStruct mapper,
service, controller. For Teacher and Batch, the DTO takes subject
references as `subjectIds: List<Long>`. For Room, it takes
`fixedBatchId`.

| Who | Resources | When |
|---|---|---|
| Bhavya | Foundation: `GlobalExceptionHandler` (`@RestControllerAdvice`) + `ApiError` JSON shape; `ResourceNotFoundException`, 400 for validation errors, 409 for unique-key and foreign-key violations; shared MapStruct config | until 2026-10-18 |
| Aman | Teachers (faculty) | 2026-10-26 – 2026-11-08 |
| Bhavishya | Rooms, then Subjects | 2026-10-26 – 2026-11-22 |
| — | **Batches: no owner yet** (see open items) | — |

### Blocking fixes (Bhavya, part of the service layer task)

1. ✅ Every `*Entity` has `@NoArgsConstructor` + `@AllArgsConstructor`, so
   Hibernate can load rows.
2. ✅ `BaseEntity` is public with `@Getter`, so ids are readable.
3. ✅ `SubjectEntity.subjectType` uses `@Enumerated(EnumType.STRING)`, with
   a matching migration.

### Definition of done

- [ ] 20 endpoints, all documented in Swagger
- [ ] Validation errors return 400 with a per-field message list
- [ ] Duplicate `subjectCode` / room `name` / teacher `email` returns 409, not 500
- [ ] Integration tests (`@SpringBootTest` + MockMvc, test profile) for each controller's happy path + 404 + 400
- [ ] Create/update/delete writes rows to `audit_log`
- [ ] Backend CI green; one PR per resource
- [ ] Each member commits their own work (the weekly Form-3 report counts commits per author)

## Open items

- **Batch CRUD has no owner.** Form-2 lists faculty, room and subject
  CRUD, but not batches. Suggested owner: Aman, alongside faculty — both
  resolve `subjectIds` many-to-many. This would stretch his faculty task
  to 2026-10-26 – 2026-11-15 and push his later tasks back a week.
- **Weekly reports:** the Form-3 report counts commits per author, so
  Aman and Bhavishya need to commit from their own accounts as their
  tasks start.
