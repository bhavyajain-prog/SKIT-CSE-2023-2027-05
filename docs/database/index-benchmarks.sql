-- ============================================================================
-- Index Performance Validation Suite & Latency Metrics — KronosTT
-- Author: Bhavishya Raghav (SKIT-CSE-2023-2027-05)
-- Track: UI + Data | Sprint: Optimise the database with indexes
-- Date: October 2026
-- ============================================================================

-- Benchmark Test 1: Conflict Detection under Simulated Departmental Load
-- Simulating 50 concurrent room checks:
DO $$
DECLARE
    v_start TIMESTAMP;
    v_end TIMESTAMP;
    v_count INT;
    i INT;
BEGIN
    v_start := clock_timestamp();
    
    FOR i IN 1..50 LOOP
        SELECT COUNT(*) INTO v_count
        FROM scheduled_sessions
        WHERE room_id = (i % 20 + 1)
          AND day_of_week = (i % 5 + 1)
          AND start_slot = (i % 7 + 1);
    END LOOP;
    
    v_end := clock_timestamp();
    RAISE NOTICE '50 Room Conflict Lookups execution time: % ms', 
        EXTRACT(MILLISECONDS FROM (v_end - v_start));
END $$;

-- Benchmark Test 2: Multi-Batch Schedule Rendering Latency
DO $$
DECLARE
    v_start TIMESTAMP;
    v_end TIMESTAMP;
    v_count INT;
    b_id INT;
BEGIN
    v_start := clock_timestamp();
    
    FOR b_id IN 1..10 LOOP
        SELECT COUNT(*) INTO v_count
        FROM scheduled_sessions ss
        JOIN sessions s ON ss.session_id = s.id
        WHERE s.batch_id = b_id;
    END LOOP;
    
    v_end := clock_timestamp();
    RAISE NOTICE '10 Batch Schedule Queries execution time: % ms', 
        EXTRACT(MILLISECONDS FROM (v_end - v_start));
END $$;

-- Verification of Index Utilization via pg_stat_user_indexes:
SELECT 
    schemaname,
    relname AS table_name,
    indexrelname AS index_name,
    idx_scan AS number_of_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched
FROM pg_stat_user_indexes
WHERE relname IN ('rooms', 'sessions', 'scheduled_sessions', 'teacher_subjects', 'batch_subjects')
ORDER BY relname, indexrelname;
