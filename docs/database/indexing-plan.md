# Database Query Profiling & Indexing Strategy — KronosTT

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Optimise the database with indexes for better performance)  
**Date:** September–October 2026

---

## 1. Problem Statement & Performance Rationale

In KronosTT, the timetable scheduling engine evaluates thousands of permutations of time slots, classrooms, and teacher commitments. Without proper database indexing:
1. Every room availability check triggers a sequential table scan on `scheduled_sessions`.
2. Multi-tenant and batch lookups perform full scans on `sessions`.
3. Many-to-many relationship traversals (`teacher_subjects`, `batch_subjects`) suffer quadratic latency during session generation.

---

## 2. Identified Access Paths & Target Indexes

### 2.1 Classrooms & Lecture Halls (`rooms`)
- **Query Pattern:** Lookup room assigned to a specific batch.
- **Index:** `idx_rooms_fixed_batch_id` on `rooms (fixed_batch_id)`.
- **Benefit:** Enables instant `O(1)` index lookup when verifying dedicated classrooms.

### 2.2 Schedulable Sessions (`sessions`)
- **Query Pattern:** Aggregate all sessions belonging to a batch or teacher during timetable generation.
- **Target Indexes:**
  - `idx_sessions_batch_id` on `sessions (batch_id)`
  - `idx_sessions_teacher_id` on `sessions (teacher_id)`
  - `idx_sessions_subject_id` on `sessions (subject_id)`
  - `idx_sessions_preferred_room_id` on `sessions (preferred_room_id)`

### 2.3 Scheduled Time Slots (`scheduled_sessions`)
- **Query Pattern:** Conflict detection — "Is Room R free on Day D at Slot S?"
- **Target Composite Indexes:**
  - `idx_scheduled_sessions_room_slot` on `scheduled_sessions (room_id, day_of_week, start_slot)`
  - `idx_scheduled_sessions_day_slot` on `scheduled_sessions (day_of_week, start_slot)`
  - `idx_scheduled_sessions_session_id` on `scheduled_sessions (session_id)`

---

## 3. Migration Plan

Implement as Flyway migration `V4__add_database_indexes.sql` to ensure automatic version-controlled application across staging and production PostgreSQL instances.
