-- Flyway migration V4: Optimize database with performance indexes
-- Author: Bhavishya Raghav (SKIT-CSE-2023-2027-05)

-- 1. Index on rooms table for foreign key lookup
CREATE INDEX idx_rooms_fixed_batch_id ON rooms (fixed_batch_id);

-- 2. Indexes on sessions table (frequently queried and filtered during timetable scheduling)
CREATE INDEX idx_sessions_subject_id ON sessions (subject_id);
CREATE INDEX idx_sessions_teacher_id ON sessions (teacher_id);
CREATE INDEX idx_sessions_batch_id ON sessions (batch_id);
CREATE INDEX idx_sessions_preferred_room_id ON sessions (preferred_room_id);

-- 3. Indexes on scheduled_sessions table (queried for timetable conflict checks and view lookups)
CREATE INDEX idx_scheduled_sessions_session_id ON scheduled_sessions (session_id);
CREATE INDEX idx_scheduled_sessions_room_id ON scheduled_sessions (room_id);
CREATE INDEX idx_scheduled_sessions_room_slot ON scheduled_sessions (room_id, day_of_week, start_slot);
CREATE INDEX idx_scheduled_sessions_day_slot ON scheduled_sessions (day_of_week, start_slot);

-- 4. Indexes on join tables for reverse lookups (PK is (parent_id, subject_id))
CREATE INDEX idx_teacher_subjects_subject_id ON teacher_subjects (subject_id);
CREATE INDEX idx_batch_subjects_subject_id ON batch_subjects (subject_id);

-- 5. Index on batches for lookup and sorting
CREATE INDEX idx_batches_name_section ON batches (name, section);
