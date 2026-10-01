# Composite Index Architecture for Timetable Conflict Lookups

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Optimise the database with indexes for better performance)  
**Date:** September–October 2026

---

## 1. Index Structure Rationale

During automated timetable generation, the placement algorithm must evaluate candidate slots `(day_of_week, start_slot)` for candidate rooms `(room_id)`.

A single-column index on `room_id` is insufficient because:
1. A room will have 30–40 sessions scheduled across a week.
2. The database engine must fetch all 40 index entries and perform an in-memory bitmap filter on `day_of_week` and `start_slot`.

---

## 2. Selected Composite Index: `(room_id, day_of_week, start_slot)`

### 2.1 B-Tree Key Ordering
- **Leading Column (`room_id`):** High cardinality (40+ distinct rooms). Prunes 97.5% of index branches immediately.
- **Middle Column (`day_of_week`):** Low cardinality (Values 1 to 6). Prunes further to a single day.
- **Trailing Column (`start_slot`):** Values 1 to 8. Pinpoints the exact time slot in a single B-tree traverse.

### 2.2 Complementary Index: `(day_of_week, start_slot)`
Supports queries that analyze institution-wide concurrency:
- "How many sessions are active across the entire college on Wednesday Period 3?"
- Enables Index-Only scans without reading heap pages.

---

## 3. Storage & Maintenance Overhead Assessment
- Index Size: ~24 KB for 2,000 scheduled sessions (negligible memory footprint in PostgreSQL `shared_buffers`).
- Write Penalty: Timetable generation performs bulk writes at the end of the optimization run; write penalty during solver execution is 0 because the solver computes in-memory and writes in a single transaction.
