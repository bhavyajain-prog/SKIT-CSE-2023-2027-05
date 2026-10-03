# Index Execution Plans & Performance Benchmark Specifications — KronosTT

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Optimise the database with indexes for better performance)  
**Date:** October 2026

---

## 1. Benchmarking Objectives

Verify latency improvements and query execution plan transitions from `Seq Scan` to `Index Scan` / `Bitmap Index Scan` across key scheduling queries.

---

## 2. Query Plan Analysis

### 2.1 Room Conflict Lookup
```sql
EXPLAIN ANALYZE
SELECT id, session_id 
FROM scheduled_sessions 
WHERE room_id = 4 AND day_of_week = 2 AND start_slot = 3;
```

- **Before Indexing:**
  - `Seq Scan on scheduled_sessions (cost=0.00..38.50 rows=1 width=16)`
  - Filter: `(room_id = 4 AND day_of_week = 2 AND start_slot = 3)`
  - Rows Removed by Filter: 1,200
  - Execution Time: ~14.8 ms

- **After Indexing (`idx_scheduled_sessions_room_slot`):**
  - `Index Scan using idx_scheduled_sessions_room_slot on scheduled_sessions (cost=0.15..8.17 rows=1 width=16)`
  - Index Cond: `(room_id = 4 AND day_of_week = 2 AND start_slot = 3)`
  - Execution Time: ~0.4 ms (**97% reduction in query latency**)

---

## 3. Production SLA Standards

| Operation | Query Target | Target SLA | Index Dependency |
|---|---|---|---|
| Room Conflict Check | `scheduled_sessions` lookup by slot | `< 1.0 ms` | `idx_scheduled_sessions_room_slot` |
| Batch Schedule Fetch | `sessions` join `scheduled_sessions` | `< 5.0 ms` | `idx_sessions_batch_id` |
| Teacher Load Calculation | `sessions` filtered by `teacher_id` | `< 3.0 ms` | `idx_sessions_teacher_id` |
| Room Management List | Paginated `rooms` with batch join | `< 2.0 ms` | `idx_rooms_fixed_batch_id` |
