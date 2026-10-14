package com.kronos.engine.algorithm;

import com.kronos.engine.model.*;
import com.kronos.enums.WeekDay;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class BaseAlgo implements Algo {
    private final int maxSlots;
    private final List<Teacher> teachers;
    private final Batch batch;
    private final int workDays; // 5 -> MON-FRI, 6 -> MON-SAT
    private final List<Room> rooms;
    private final boolean fixedRooms;

    public BaseAlgo(int maxSlots, int workDays, List<Teacher> teachers, Batch batch, List<Room> rooms, boolean fixedRooms) {
        this.maxSlots = maxSlots;
        this.workDays = workDays;
        this.teachers = teachers;
        this.batch = batch;
        this.rooms = rooms;
        this.fixedRooms = fixedRooms;
    }

    // student perspective - single batch multiple teachers
    @Override
    public ScheduledResult generateTimeTable() {
        // Sort subjects according to required time in non-increasing order
        List<Subject> subjects = batch.getSubjects();
        subjects.sort(Comparator.comparingInt(Subject::getSlotDuration).reversed());

        // Allocate teachers for each subject
        Map<Long, Teacher> assigned = new HashMap<>();
        for (Subject subject : subjects) {
            List<Teacher> eligible = teachers.stream().filter(t -> t.getSubjects().stream().anyMatch(s -> s.getId() == subject.getId())).toList();
            if (eligible.isEmpty())
                throw new IllegalArgumentException("Subject " + subject.getId() + " has no eligible teacher");
            Teacher randPick = eligible.get(new SecureRandom().nextInt((eligible.size())));
            assigned.put(subject.getId(), randPick);
        }

        // Filter rooms with more than required capacity
        PriorityQueue<Room> eligibleRooms = rooms.stream()
                .filter(room -> room.getCapacity() >= batch.getStrength() && room.getFixedBatch() == null)
                .collect(Collectors.toCollection(() ->
                        new PriorityQueue<>(Comparator.comparingInt(Room::getCapacity))
                ));
        if (eligibleRooms.isEmpty())
            throw new IllegalStateException("No room available");

        // 2 possibilities - single room
        // TODO: When multiple batches will be there, we need to make sure nothing collides
        Room selectedRoom;
        if (fixedRooms) {
            selectedRoom = eligibleRooms.poll(); // This will be used everywhere for this batch
            selectedRoom = new Room(selectedRoom, batch);
        } else {
            selectedRoom = eligibleRooms.poll(); // This will be used everywhere for this batch
            selectedRoom = new Room(selectedRoom, batch);
        }

        // Build all sessions as per subject weights
        List<Session> allSessions = new LinkedList<>();
        long sessionId = 1L;
        for (Subject subject : subjects) {
            Teacher teacher = assigned.get(subject.getId());
            for (int i = 0; i < subject.getWeight(); i++) {
                allSessions.add(Session.builder().id(sessionId++).subject(subject).teacher(teacher).batch(batch).slotDuration(subject.getSlotDuration()).build());
            }
        }

        // slot allocations with collision detection
        int totSessions = workDays * maxSlots;
        boolean[][] filled = new boolean[workDays][maxSlots];
        // Track teacher allocations: teacherId -> [day][slot]
        Map<Long, boolean[][]> teacherAllocations = new HashMap<>();
        // Track room allocations: roomId -> [day][slot]
        Map<Long, boolean[][]> roomAllocations = new HashMap<>();

        List<ScheduledSession> scheduledSessions = new LinkedList<>();
        SecureRandom random = new SecureRandom();

        for (Session session : allSessions) {
            long teacherId = session.getTeacher().getId();
            int duration = session.getSlotDuration();
            int day, start;
            int attempts = 0;
            int maxAttempts = totSessions * 10;

            do {
                if (attempts++ > maxAttempts)
                    throw new RuntimeException("Too many attempts for " + session.getSubject().getName());
                int rand = random.nextInt(totSessions);
                day = rand / maxSlots;
                start = rand % maxSlots;
            } while (!canPlace(filled, teacherAllocations, roomAllocations, teacherId, selectedRoom.getId(), day, start, duration));

            // Mark slots as filled
            for (int i = 0; i < duration; i++) {
                filled[day][start + i] = true;

                // Mark teacher as allocated
                teacherAllocations.computeIfAbsent(teacherId, k -> new boolean[workDays][maxSlots]);
                teacherAllocations.get(teacherId)[day][start + i] = true;

                // Mark room as allocated
                roomAllocations.computeIfAbsent(selectedRoom.getId(), k -> new boolean[workDays][maxSlots]);
                roomAllocations.get(selectedRoom.getId())[day][start + i] = true;
            }

            scheduledSessions.add(ScheduledSession.builder()
                    .session(session)
                    .weekDay(WeekDay.values()[day])
                    .startSlot(start + 1)
                    .assignedRoom(selectedRoom)
                    .build());
        }
        return ScheduledResult.builder().sessions(scheduledSessions).build();
    }

    private boolean canPlace(boolean[][] filled,
                             Map<Long, boolean[][]> teacherAllocations,
                             Map<Long, boolean[][]> roomAllocations,
                             long teacherId, long roomId,
                             int day, int start, int duration) {
        if (start + duration > maxSlots) return false;

        for (int i = 0; i < duration; i++) {
            // Check if slot is filled for this batch
            if (filled[day][start + i]) return false;

            // Check if teacher is already allocated at this time
            boolean[][] teacherSchedule = teacherAllocations.get(teacherId);
            if (teacherSchedule != null && teacherSchedule[day][start + i]) return false;

            // Check if room is already allocated at this time
            boolean[][] roomSchedule = roomAllocations.get(roomId);
            if (roomSchedule != null && roomSchedule[day][start + i]) return false;
        }
        return true;
    }
}
