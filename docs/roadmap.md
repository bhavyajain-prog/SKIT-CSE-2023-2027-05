# Roadmap — KronosTT (SKIT-CSE-2023-2027-05)

12-week plan for a team of 4. Week 1 = 2026-08-31, so the project ends
2026-11-22. Each phase is one week.

## Team tracks

| Track | Owner | Scope |
|---|---|---|
| Engine | Aaditya | `com.kronos.engine`: TimeTableState, constraints, SessionGenerator, placement algorithms |
| Platform / backend | Bhavya | Infra, CI/CD, shared backend foundations, integration, reviews |
| Feature dev | Member C, Member D | CRUD APIs, frontend screens, exports |

`kronostt-ref` is the reference implementation. **Its engine
(`engine/SessionGenerator`, `engine/algorithm/*`, `engine/constraints/*`,
`TimeTableState`) is out of scope for everyone except the engine track.**
Everything else in it (services, mappers, DTOs, controllers,
`TimetableJobEntity`, Docker/deploy) is fair to port and improve.

## Status snapshot (as of 2026-09-24)

**Done on `main`**
- Monorepo scaffold, backend + frontend CI (path-filtered, PR-gated)
- JPA entities + repositories for Subject, Teacher, Batch, Room, Session, ScheduledSession
- Swagger, CORS, `/health` endpoint + frontend connection check
- Flyway (`V1__init_schema`, `V2__create_audit_log_table`), `ddl-auto=validate`
- Generic `audit_log` via JPA entity listener on `BaseEntity`
- Engine models + `Constraints` interface + initial `TimeTableState`
- Weekly Form-3 report automation

**In flight**
- `ci-cd/pipeline-dev`: concurrency, timeouts, least-privilege perms (not merged)
- `dev/aaditya`: `TimeTableState` scoring, `canPlace`, `placeSession`
  (≈180 lines, 3 commits ahead of `main`)

**Engine remaining (vs. reference):** 4 concrete constraints
(teacher unavailability, daily load, subject spacing, preferred slot),
`SessionGenerator`, `BaseAlgo` / `HeuristicPlacementAlgo` /
`ConstraintPropagationAlgo` / `MultipleSchedulesAlgo`, and their tests.

## Phases

| Wk | Dates | Phase | Engine track (parallel) | Status |
|---|---|---|---|---|
| 1 | Aug 31 – Sep 6 | Scaffold, CI, standards docs | — | Done |
| 2 | Sep 7 – Sep 13 | Entities, repositories, Swagger | Engine models + enums | Done |
| 3 | Sep 14 – Sep 20 | CORS, health check, frontend API client | `TimeTableState`, `Constraints` | Done |
| 4 | Sep 21 – Sep 27 | Flyway, audit log, CI optimisation | `TimeTableState` scoring | Done / in review |
| **5** | **Sep 28 – Oct 4** | **Master-data CRUD API** (see below) | Concrete constraints | **Next** |
| 6 | Oct 5 – Oct 11 | Frontend foundation: router, layout, typed API client, Subjects + Rooms screens | `SessionGenerator` + tests | |
| 7 | Oct 12 – Oct 18 | Teachers + Batches screens (subject multi-select), CSV bulk import endpoint | `BaseAlgo`, `HeuristicPlacementAlgo` | |
| 8 | Oct 19 – Oct 25 | Timetable jobs: `timetable_jobs` table (V3), preferences JSON, async job + status polling API, engine contract/stub | `ConstraintPropagationAlgo`, `MultipleSchedulesAlgo` | |
| 9 | Oct 26 – Nov 1 | **Engine integration**: wire generator + algorithm into `TimetableService`, persist scheduled/unscheduled sessions | Integration support, full-load tests | |
| 10 | Nov 2 – Nov 8 | Timetable views (by batch / teacher / room), manual lock/move of sessions, PDF/Excel export | Performance tuning | |
| 11 | Nov 9 – Nov 15 | Auth (Spring Security + JWT, admin/viewer roles), Docker + deploy workflow | Algorithm selection / tuning | |
| 12 | Nov 16 – Nov 22 | Hardening: test coverage, bug bash, docs, demo, final report | Bug fixes | |

Weeks 8 and 9 are the critical path. Week 8 has to lock the interface
between `TimetableService` and the engine, so both tracks can work
against it independently.

## Week 5 — Master-data CRUD API

**Goal:** full REST CRUD for the four master-data resources, so that
week 6 frontend work and week 8 job setup have real data to use.

### Scope

Endpoints under `/api/v1/{subjects,rooms,teachers,batches}`:

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

### Split

| Who | Work | Days |
|---|---|---|
| Bhavya | **Foundation first**, then review. Items: `GlobalExceptionHandler` (`@RestControllerAdvice`) + `ApiError` JSON shape; `ResourceNotFoundException`, 400 for validation errors, 409 for unique-key and foreign-key violations; shared MapStruct config; entity fixes listed below | Mon–Tue, then review |
| Member C | Subject + Room: DTO, mapper, service, controller, tests | Tue–Fri |
| Member D | Teacher + Batch (many-to-many `subjectIds` resolution): DTO, mapper, service, controller, tests | Tue–Fri |
| Aaditya | Engine only: concrete constraints. Hands the `BatchMapper` on `dev/aaditya` over to Member D (see below) | — |

### Blocking fixes to do first (Bhavya, day 1)

1. **Entities have no no-arg constructor.** They use `@Builder` + `@Data`
   only, and Hibernate needs a no-arg constructor to load rows. Add
   `@NoArgsConstructor` + `@AllArgsConstructor` to every `*Entity`.
2. **`BaseEntity.id` has no getter.** The class is package-private with a
   `protected` field, so mappers and services can't read ids. Add
   `@Getter` (and make the class `public`).
3. **`SubjectEntity.subjectType` has no `@Enumerated`**, so it is stored
   as an ordinal. Decide on `EnumType.STRING` now, while no real data
   exists, and add a `V3` migration to match.

### Note on `dev/aaditya`

`service/mapper/BatchMapper.java` on that branch does not compile:
- It imports `com.kronos.persistence.model.*` (the package is `persistence.entity`).
- It has no `@Mapper` annotation.
- `BatchDto` doesn't exist.
- It calls `new SubjectEntity()` and `setId`, which blocking fixes 1 and 2 would need first.

It belongs to the CRUD work, not the engine. Drop it from `dev/aaditya`
before that branch's next PR. Member D rebuilds it properly this week.

### Definition of done

- [ ] 20 endpoints, all documented in Swagger
- [ ] Validation errors return 400 with a per-field message list
- [ ] Duplicate `subjectCode` / room `name` / teacher `email` returns 409, not 500
- [ ] Integration tests (`@SpringBootTest` + MockMvc, test profile) for each controller's happy path + 404 + 400
- [ ] Create/update/delete writes rows to `audit_log`
- [ ] Backend CI green; one PR per resource pair (`feat/crud-subject-room`, `feat/crud-teacher-batch`, `feat/api-error-handling`)
- [ ] Each member commits their own work (the weekly Form-3 report counts commits per author)
