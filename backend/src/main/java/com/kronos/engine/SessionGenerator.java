package com.kronos.engine;

import com.kronos.engine.model.*;

import java.util.ArrayList;
import java.util.List;

public class SessionGenerator {

    private final List<Teacher> teachers;
    private final List<Batch> batches;
    private final List<Room> rooms; // for room availability
    private final TimetablePreferences prefs;

    private long sessionId = 1L;

    public SessionGenerator(List<Teacher> teachers, List<Batch> batches, List<Room> rooms, TimetablePreferences prefs) {
        this.teachers = teachers;
        this.batches = batches;
        this.rooms = rooms;
        this.prefs = prefs;
    }

    public List<Session> generateAllSessions() {
        List<Session> allSessions = new ArrayList<>();
        for (Batch batch : batches) {
            for (Subject subject : batch.getSubjects()) {
                Teacher assignedTeacher = resolveTeacher(batch, subject);
                Room preferredRoom = resolvePreferredRoom(batch, subject, assignedTeacher);
                List<Integer> sessionDurations = resolveDurations(subject);
                for (int duration : sessionDurations) {
                    allSessions.add(Session.builder()
                            .id(sessionId++)
                            .batch(batch)
                            .teacher(assignedTeacher)
                            .subject(subject)
                            .slotDuration(duration)
                            .preferredRoom(preferredRoom)
                            .build());
                }
            }
        }
        return allSessions;
    }

    private Teacher resolveTeacher(Batch batch, Subject subject) {
        // Check user preferences first
        String key = batch.getId() + "_" + subject.getId();
        if (prefs.getTeacherAllocations().containsKey(key)) {
            long teacherId = prefs.getTeacherAllocations().get(key);
            return teachers.stream().filter(t -> t.getId() == teacherId).findFirst().orElseThrow(
                    () -> new RuntimeException("Teacher not found with id " + teacherId)
            );
        }
        // Fallback: Pick the first capable teacher (or throw an error if strict allocation is required)
        return teachers.stream()
                .filter(t -> t.getSubjects().stream().anyMatch(s -> s.getId() == subject.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No teacher mapped for " + subject.getName()));
    }

    private Room resolvePreferredRoom(Batch batch, Subject subject, Teacher teacher) {
        // Hierarchy of Needs: Subject Labs > Teacher's Lair > Batch Home Room
        if (prefs.getSubjectRooms().containsKey(subject.getId())) {
            return getRoomById(prefs.getSubjectRooms().get(subject.getId()));
        }
        if (prefs.getTeacherRooms().containsKey(teacher.getId())) {
            return getRoomById(prefs.getTeacherRooms().get(teacher.getId()));
        }
        if (prefs.getBatchHomeRooms().containsKey(batch.getId())) {
            return getRoomById(prefs.getBatchHomeRooms().get(batch.getId()));
        }
        return null; // The algorithm will find an available room dynamically
    }

    private List<Integer> resolveDurations(Subject subject) {
        // If a subject needs 3 hours, do we make three 1-hr sessions, or one 2-hr and one 1-hr?
        if (prefs.getCustomSplits().containsKey(subject.getId())) {
            return prefs.getCustomSplits().get(subject.getId());
        }

        // Fallback: Default to whatever the Subject POJO dictates (e.g., 3 separate 1-hour blocks)
        List<Integer> standardSplit = new ArrayList<>();
        for (int i = 0; i < subject.getWeight(); i++) {
            standardSplit.add(subject.getSlotDuration());
        }
        return standardSplit;
    }

    // Won't be needed once repo layer is set up
    private Room getRoomById(long id) {
        return rooms.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }

}
