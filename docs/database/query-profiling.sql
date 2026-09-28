-- ============================================================================
-- Query Profiling and Join Bottleneck Analysis — KronosTT
-- Author: Bhavishya Raghav (SKIT-CSE-2023-2027-05)
-- Track: UI + Data | Sprint: Optimise the database with indexes
-- Date: September 2026
-- ============================================================================

-- Scenario 1: Room Conflict Detection Query
-- During schedule permutation, the engine validates if a room is occupied.
-- Unindexed: Sequential Scan on scheduled_sessions table.
EXPLAIN ANALYZE
SELECT id, session_id, room_id, day_of_week, start_slot
FROM scheduled_sessions
WHERE room_id = 5 
  AND day_of_week = 1 
  AND start_slot = 3;

-- Scenario 2: Batch Schedule Reconstruction Query
-- Used by the frontend matrix grid to render a full week for a single batch.
-- Joins: sessions -> scheduled_sessions -> subjects -> teachers -> rooms.
EXPLAIN ANALYZE
SELECT 
    ss.day_of_week,
    ss.start_slot,
    sub.subject_code,
    sub.subject_name,
    t.first_name || ' ' || COALESCE(t.last_name, '') AS teacher_name,
    r.name AS room_name,
    b.name AS batch_name
FROM scheduled_sessions ss
JOIN sessions s ON ss.session_id = s.id
JOIN subjects sub ON s.subject_id = sub.id
JOIN teachers t ON s.teacher_id = t.id
JOIN rooms r ON ss.room_id = r.id
JOIN batches b ON s.batch_id = b.id
WHERE s.batch_id = 3
ORDER BY ss.day_of_week, ss.start_slot;

-- Scenario 3: Teacher Workload & Conflict Verification Query
-- Validates that a teacher is not double-booked across different rooms/batches.
EXPLAIN ANALYZE
SELECT 
    s.teacher_id,
    ss.day_of_week,
    ss.start_slot,
    COUNT(ss.id) AS active_classes
FROM scheduled_sessions ss
JOIN sessions s ON ss.session_id = s.id
GROUP BY s.teacher_id, ss.day_of_week, ss.start_slot
HAVING COUNT(ss.id) > 1;

-- Scenario 4: Fixed Room Batch Association Verification
-- Queries whether a batch already has a dedicated room.
EXPLAIN ANALYZE
SELECT id, name, capacity, fixed_batch_id
FROM rooms
WHERE fixed_batch_id = 2;

-- Scenario 5: Reverse Join Table Traversal for Subject Dependencies
-- Used when calculating teacher eligibility for course assignments.
EXPLAIN ANALYZE
SELECT t.id, t.first_name, t.email
FROM teachers t
JOIN teacher_subjects ts ON t.id = ts.teacher_id
WHERE ts.subject_id = 12;
