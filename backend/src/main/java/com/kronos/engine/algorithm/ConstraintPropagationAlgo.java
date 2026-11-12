package com.kronos.engine.algorithm;

import com.kronos.engine.TimeTableState;
import com.kronos.engine.model.Room;
import com.kronos.engine.model.ScheduledSession;
import com.kronos.engine.model.Session;
import com.kronos.enums.WeekDay;
import org.hibernate.mapping.Constraint;

import java.util.*;

public class ConstraintPropagationAlgo extends AbstractPlacementAlgo{
    private static final int LCV_SAMPLE_LIMIT = 50;
    private final TimeTableState state;
    private final List<Room> rooms;
    private final int weekDays;
    private final int maxSlots;
    private final int lunchStart;
    private final List<Constraint> constraints;
    // Track placed sessions for conflict detection
    private final List<PlacedSession> placedSessions = new ArrayList<>();
    private final Set<Long> placedSessionIds = new HashSet<>();

    public ConstraintPropagationAlgo(List<Session> sessions, List<ScheduledSession> lockedSessions, List<Room> rooms, int weekDays, int maxSlots, int lunchStart, List<Constraint> constraints) {

        super(sessions, lockedSessions != null ? lockedSessions : new ArrayList<>());

        this.rooms = rooms;
        this.weekDays = weekDays;
        this.maxSlots = maxSlots;
        this.lunchStart = lunchStart;

        // Default constraints to empty list if null
        this.constraints = constraints != null ? constraints : new ArrayList<>();

        this.state = new TimeTableState(weekDays, maxSlots, lunchStart);
    }

    @Override
    protected void sortSessionsByDifficulty() {
        // Pre-compute workload statistics (static)
        // Statistics for constraint tightness calculation
        Map<Long, Integer> teacherTotalHours = new HashMap<>();
        Map<Long, Integer> batchTotalHours = new HashMap<>();
        Map<Long, Integer> teacherSubjectCount = new HashMap<>();
        Map<Long, Integer> batchSubjectCount = new HashMap<>();

        for (Session session : sessions) {
            long teacherId = session.getTeacher().getId();
            long batchId = session.getBatch().getId();
            int duration = session.getSlotDuration();

            teacherTotalHours.merge(teacherId, duration, Integer::sum);
            batchTotalHours.merge(batchId, duration, Integer::sum);
            teacherSubjectCount.merge(teacherId, 1, Integer::sum);
            batchSubjectCount.merge(batchId, 1, Integer::sum);
        }

        // Calculate constraint tightness for each session
        // Higher score = more constrained = should be placed first
        Map<Session, Double> constraintScore = new HashMap<>();
        for (Session session : sessions) {
            long teacherId = session.getTeacher().getId();
            long batchId = session.getBatch().getId();

            // Factors that increase constraint tightness:
            // 1. Longer duration (harder to fit)
            double durationScore = session.getSlotDuration() * 10.0;

            // 2. Teacher with more total hours (more constrained resource)
            double teacherLoadScore = teacherTotalHours.getOrDefault(teacherId, 0) / 10.0;

            // 3. Batch with more total hours
            double batchLoadScore = batchTotalHours.getOrDefault(batchId, 0) / 10.0;

            // 4. Teacher teaching fewer unique subjects (more constrained)
            double teacherFlexScore = 20.0 / teacherSubjectCount.getOrDefault(teacherId, 1);

            // 5. Batch taking fewer unique subjects
            double batchFlexScore = 20.0 / batchSubjectCount.getOrDefault(batchId, 1);

            double totalScore = durationScore + teacherLoadScore + batchLoadScore + teacherFlexScore + batchFlexScore;

            constraintScore.put(session, totalScore);
        }

        // Sort by constraint score DESC (most constrained first)
        // Then by slotDuration DESC (longer sessions first)
        sessions.sort(Comparator.comparingDouble((Session s) -> constraintScore.getOrDefault(s, 0.0)).reversed().thenComparingInt(Session::getSlotDuration).reversed().thenComparingLong(s -> s.getTeacher().getId()).thenComparingLong(s -> s.getBatch().getId()));
    }

    @Override
    protected boolean attemptPlacing(Session session, List<ScheduledSession> scheduledSessions) {
        // Compute all valid slots for this session
        List<TimeSlot> validSlots = computeValidSlots(session);

        if (validSlots.isEmpty()) {
            return false;
        }

        // LCV: Select slot that constrains fewest other unplaced sessions
        TimeSlot bestSlot = selectLeastConstrainingValue(session, validSlots);

        if (bestSlot != null) {
            executePlacement(session, bestSlot.room, bestSlot.day, bestSlot.startSlot, scheduledSessions);
            return true;
        }

        // Fallback: use first valid slot
        TimeSlot fallback = validSlots.getFirst();
        executePlacement(session, fallback.room, fallback.day, fallback.startSlot, scheduledSessions);
        return true;
    }

    /**
     * Compute all valid (day, slot, room) combinations for a session
     * Considers collisions and any registered constraints
     */
    private List<TimeSlot> computeValidSlots(Session session) {
        List<TimeSlot> validSlots = new ArrayList<>();
        int duration = session.getSlotDuration();

        // Pre-filter rooms by capacity
        List<Room> eligibleRooms = rooms.stream().filter(r -> r.getCapacity() >= session.getBatch().getStrength()).toList();

        for (int day = 0; day < weekDays; day++) {
            // Slots must fit within day considering duration
            for (int start = 0; start <= maxSlots - duration; start++) {
                // Check lunch break constraint
                int lastSlot = start + duration - 1;
                if (start <= lunchStart && lastSlot >= lunchStart) {
                    continue;
                }

                for (Room room : eligibleRooms) {
                    // Check collisions and constraints
                    if (isValidPlacement(session, room, day, start)) {
                        validSlots.add(new TimeSlot(day, start, room));
                    }
                }
            }
        }

        return validSlots;
    }

    /**
     * Check if placement is valid considering collisions and constraints
     */
    private boolean isValidPlacement(Session session, Room room, int day, int start) {
        // First check basic collisions using TimeTableState
        if (!state.canPlace(session, room, day, start)) {
            return false;
        }

        // Check for conflicts with already placed sessions (redundant but safer)
        int duration = session.getSlotDuration();
        for (PlacedSession placed : placedSessions) {
            if (placed.day != day) continue;

            int placedEnd = placed.startSlot + placed.session.getSlotDuration();
            int thisEnd = start + duration;
            boolean timeOverlap = !(placedEnd <= start || placed.startSlot >= thisEnd);

            if (!timeOverlap) continue;

            // Check resource conflicts
            boolean sameTeacher = placed.session.getTeacher().getId() == session.getTeacher().getId();
            boolean sameBatch = placed.session.getBatch().getId() == session.getBatch().getId();
            boolean sameRoom = placed.room.getId() == room.getId();

            if (sameTeacher || sameBatch || sameRoom) {
                return false;
            }
        }

        // Then check custom constraints if any
        if (!constraints.isEmpty()) {
            int score = state.getHeuristicScore(session, room, day, start, constraints);
            return score > -(Integer.MAX_VALUE / 2);
        }

        return true;
    }

    /**
     * LCV: Select slot that removes fewest options from other unplaced sessions
     * Also considers placement quality (preferred room, etc.)
     */
    private TimeSlot selectLeastConstrainingValue(Session session, List<TimeSlot> validSlots) {
        if (validSlots.isEmpty()) return null;
        if (validSlots.size() == 1) return validSlots.getFirst();

        // Get unplaced sessions (sessions not yet in placedSessions)
        List<Session> unplaced = sessions.stream().filter(s -> !placedSessionIds.contains(s.getId())).filter(s -> s != session).toList();

        TimeSlot bestSlot = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        // Sample limited slots for performance
        int sampleSize = Math.min(validSlots.size(), LCV_SAMPLE_LIMIT);
        List<TimeSlot> sampledSlots = validSlots.subList(0, sampleSize);

        for (TimeSlot slot : sampledSlots) {
            // Base score from LCV (negative impact)
            double score = -calculateConstraintImpact(session, slot, unplaced);

            // Bonus for preferred room
            if (session.getPreferredRoom() != null && slot.room.getId() == session.getPreferredRoom().getId()) {
                score += 100;
            }

            // Bonus for good capacity match (minimize wasted seats)
            int wastedSeats = slot.room.getCapacity() - session.getBatch().getStrength();
            score -= wastedSeats / 10.0;

            // Bonus for morning slots for heavy subjects, afternoon for light
            int midPoint = maxSlots / 2;
            boolean isHeavy = session.getSlotDuration() >= 2 || (session.getSubject() != null && session.getSubject().getWeight() >= 3);
            if (isHeavy && slot.startSlot < midPoint) {
                score += 5; // Prefer morning for heavy subjects
            } else if (!isHeavy && slot.startSlot >= midPoint) {
                score += 3; // Prefer afternoon for light subjects
            }

            // Check for consecutive same-subject penalty
            boolean consecutiveSameSubject = hasAdjacentSameSubject(session, slot.day, slot.startSlot);
            if (consecutiveSameSubject) {
                score -= 50;
            }

            if (score > bestScore) {
                bestScore = score;
                bestSlot = slot;
            }
        }

        return bestSlot;
    }

    /**
     * Check if placing at this slot would create consecutive same-subject sessions
     */
    private boolean hasAdjacentSameSubject(Session session, int day, int startSlot) {
        long subjectId = session.getSubject().getId();

        for (PlacedSession placed : placedSessions) {
            if (placed.day != day) continue;
            if (placed.session.getBatch().getId() != session.getBatch().getId()) continue;
            if (placed.session.getSubject().getId() != subjectId) continue;

            // Check if adjacent
            int placedEnd = placed.startSlot + placed.session.getSlotDuration();
            if (placedEnd == startSlot || placed.startSlot == startSlot + session.getSlotDuration()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Calculate how many valid slots would be removed from other sessions
     * if we place this session at the given slot
     */
    private int calculateConstraintImpact(Session session, TimeSlot slot, List<Session> unplacedSessions) {
        int impact = 0;
        int duration = session.getSlotDuration();

        for (Session other : unplacedSessions) {
            // Check if this placement conflicts with other session's potential slots
            boolean sameTeacher = other.getTeacher().getId() == session.getTeacher().getId();
            boolean sameBatch = other.getBatch().getId() == session.getBatch().getId();

            if (!sameTeacher && !sameBatch) {
                // Check room conflict
                if (other.getPreferredRoom() == null || slot.room.getId() != other.getPreferredRoom().getId()) {
                    continue;
                }
                // Potential room conflict
            }

            // Count estimated conflicting slots
            int otherDuration = other.getSlotDuration();
            int otherEnd = slot.startSlot + otherDuration;
            int thisEnd = slot.startSlot + duration;

            // Overlap check
            boolean timeOverlap = !(otherEnd <= slot.startSlot || slot.startSlot >= thisEnd);

            if (timeOverlap) {
                if (sameTeacher || sameBatch) {
                    impact += 2; // Higher impact for teacher/batch conflicts
                } else {
                    impact += 1;
                }
            }
        }

        return impact;
    }

    /**
     * Execute placement and track it
     */
    private void executePlacement(Session session, Room room, int day, int start, List<ScheduledSession> scheduledSessions) {
        state.placeSession(session, room, day, start, session.getSlotDuration());

        PlacedSession placed = new PlacedSession(session, room, day, start);
        placedSessions.add(placed);
        placedSessionIds.add(session.getId());

        scheduledSessions.add(ScheduledSession.builder().session(session).startSlot(start).weekDay(WeekDay.values()[day]).assignedRoom(room).build());
    }

    /**
     * Represents a valid (day, slot, room) combination
     */
    private record TimeSlot(int day, int startSlot, Room room) {
    }

    /**
     * Represents a placed session for conflict detection
     */
    private record PlacedSession(Session session, Room room, int day, int startSlot) {
    }
}
