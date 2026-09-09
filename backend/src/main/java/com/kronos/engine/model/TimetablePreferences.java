package com.kronos.engine.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class TimetablePreferences {
    // Key: "BatchId_SubjectId", Value: TeacherId
    private Map<String, Long> teacherAllocations = new HashMap<>();

    // Key: Entity ID, Value: RoomId
    private Map<Long, Long> batchHomeRooms = new HashMap<>();
    private Map<Long, Long> subjectRooms = new HashMap<>(); // For Labs
    private Map<Long, Long> teacherRooms = new HashMap<>();

    // Key: SubjectId, Value: List of block durations (e.g., [2, 1, 1] for 4 credits)
    private Map<Long, List<Integer>> customSplits = new HashMap<>();

    // Sessions that already have a fixed Day and Slot
    private List<ScheduledSession> preLockedSessions = new ArrayList<>();
}