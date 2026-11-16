package com.kronos.engine;

import com.kronos.engine.model.Room;
import com.kronos.engine.model.Session;
import lombok.Data;
import lombok.Getter;
import com.kronos.engine.constraints.Constraints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class TimeTableState {
    @Getter
    private final int workDays;
    @Getter
    private final int maxSlots;
    private final int LUNCH_START;

    private final Map<Long, Session[][]> teacherSchedules = new HashMap<>();
    private final Map<Long, Session[][]> batchSchedules = new HashMap<>();
    private final Map<Long, Session[][]> roomSchedules = new HashMap<>();

    public TimeTableState(int workDays, int maxSlots, int LUNCH_START) {
        this.workDays = workDays;
        this.maxSlots = maxSlots;
        this.LUNCH_START = LUNCH_START;
    }


private Session[][] getEmptyGrid() {
    return new Session[workDays][maxSlots];
}

public int getHeuristicScore(Session session, Room room, int day, int start, List<Constraints> constraints) {
    // 3. Collision = -INF (Check this first to save CPU cycles!)
    if (!canPlace(session, room, day, start)) {
        return -(Integer.MAX_VALUE / 2);
    }

    int score = 0;
    int duration = session.getSlotDuration();

    // Safely fetch grids. If they don't exist yet, we get an empty grid.
    Session[][] tGrid = teacherSchedules.getOrDefault(session.getTeacher().getId(), getEmptyGrid());
    Session[][] bGrid = batchSchedules.getOrDefault(session.getBatch().getId(), getEmptyGrid());
    Session[][] rGrid = roomSchedules.getOrDefault(room.getId(), getEmptyGrid());

    // 1. Constraints
    if (constraints != null && !constraints.isEmpty()) {
        for (Constraints constraint : constraints) {
            score += constraint.evaluate(session, room, day, start, this);
        }
    }

    // 2. Preferred room is allocated = +1
    if (session.getPreferredRoom() != null && room.getId() == session.getPreferredRoom().getId()) {
        score += 1;
    }

    // 4. Same batch had previous session in same room = +10 (Minimizes walking)
    if (start > 0 && rGrid[day][start - 1] != null) {
        if (rGrid[day][start - 1].getBatch().getId() == session.getBatch().getId()) {
            score += 10;
        }
    }

    // 5. Repeating session (Subject) in same day = -1
    for (int i = 0; i < maxSlots; i++) {
        if (bGrid[day][i] != null && bGrid[day][i].getSubject().getId() == session.getSubject().getId()) {
            score -= 1;
            break; // Only penalize once per day
        }
    }

    // 6. Consecutive allocation of SAME subject = -50 (Avoid back-to-back duplicate lectures)
    // Check slot immediately before
    if (start > 0 && bGrid[day][start - 1] != null && bGrid[day][start - 1].getSubject().getId() == session.getSubject().getId()) {
        score -= 50;
    }
    // Check slot immediately after (accounting for this session's duration)
    int endSlot = start + duration;
    if (endSlot < maxSlots && bGrid[day][endSlot] != null && bGrid[day][endSlot].getSubject().getId() == session.getSubject().getId()) {
        score -= 50;
    }

    // 7. Consecutive teacher allotment (>=3) = -10
    int consecutiveTeacherSlots = duration; // Start with current session's length
    // Count backwards
    for (int i = start - 1; i >= 0; i--) {
        if (tGrid[day][i] != null) consecutiveTeacherSlots++;
        else break;
    }
    // Count forwards
    for (int i = start + duration; i < maxSlots; i++) {
        if (tGrid[day][i] != null) consecutiveTeacherSlots++;
        else break;
    }
    if (consecutiveTeacherSlots >= 3) {
        score -= 10;
    }

    // 8. Full free day preservation
    // If placing this class makes it the ONLY class on this day for the batch, penalize it.
    // This encourages the algorithm to pack classes into already active days, leaving other days entirely free.
    boolean isDayCurrentlyEmpty = true;
    for (int i = 0; i < maxSlots; i++) {
        if (bGrid[day][i] != null) {
            isDayCurrentlyEmpty = false;
            break;
        }
    }
    if (isDayCurrentlyEmpty) {
        score -= 5; // Using -5 instead of -INF so it's still possible if no other options exist.
    }

    // 9. Vertically same allotments = +10 (e.g., Math is always at 9 AM)
    for (int d = 0; d < workDays; d++) {
        if (d != day && bGrid[d][start] != null && bGrid[d][start].getSubject().getId() == session.getSubject().getId()) {
            score += 10;
        }
    }

    // 10. Contiguity = +5
    // Reward placing classes next to existing classes
    boolean hasAdjacentClass = start > 0 && bGrid[day][start - 1] != null;
    if (endSlot < maxSlots && bGrid[day][endSlot] != null) hasAdjacentClass = true;
    if (hasAdjacentClass) score += 5;

    // 11. Capacity optimization
    // Penalty for wasted capacity
    int wastedSeats = room.getCapacity() - session.getBatch().getStrength();
    score -= wastedSeats / 10;

    // 12. Morning-Afternoon rewards
    // Labs in morning and light subjects in Afternoon
    if (session.getSlotDuration() >= 2 || session.getSubject().getWeight() >= 3) {
        if (start < maxSlots / 2) score += 5;
        else score -= 5;
    } else {
        if (start >= maxSlots / 2) score += 5;
    }

    // 13. Teacher Burnout Penalty
    int teacherClassesToday = 0;
    for (int i = 0; i < maxSlots; i++) {
        if (tGrid[day][i] != null) teacherClassesToday++;
    }
    if (teacherClassesToday + session.getSlotDuration() > 4) {
        score -= 20;
    }
    return score;
}


public boolean canPlace(Session session, Room room, int day, int start) {
    int duration = session.getSlotDuration();
    if (start + duration > maxSlots) return false;
    // The Golden Lunch Check
    int lastSlot = start + duration - 1;
    if (start <= LUNCH_START && lastSlot >= LUNCH_START) return false;

    Session[][] teacherBoard = teacherSchedules.getOrDefault(session.getTeacher().getId(), getEmptyGrid());
    Session[][] batchBoard = batchSchedules.getOrDefault(session.getBatch().getId(), getEmptyGrid());
    Session[][] roomBoard = roomSchedules.getOrDefault(room.getId(), getEmptyGrid());

    for (int i = 0; i < duration; i++) {
        if (teacherBoard[day][start + i] != null || batchBoard[day][start + i] != null || roomBoard[day][start + i] != null)
            return false;
    }
    return true;
}
public void placeSession(Session session, Room room, int day, int start, int duration) {
    Session[][] teacherBoard = teacherSchedules.computeIfAbsent(session.getTeacher().getId(), k -> getEmptyGrid());
    Session[][] batchBoard = batchSchedules.computeIfAbsent(session.getBatch().getId(), k -> getEmptyGrid());
    Session[][] roomBoard = roomSchedules.computeIfAbsent(room.getId(), k -> getEmptyGrid());

    for (int i = 0; i < duration; i++) {
        teacherBoard[day][start + i] = session;
        batchBoard[day][start + i] = session;
        roomBoard[day][start + i] = session;
    }
}

public int getTeacherHoursOnDay(long teacherId, int day) {
    Session[][] grid = teacherSchedules.get(teacherId);
    if (grid == null) return 0;

    int hours = 0;
    for (int s = 0; s < maxSlots; s++) {
        if (grid[day][s] != null) hours++;
    }
    return hours;
}

public Session getBatchSession(long batchId, int day, int slot) {
    if (day >= workDays || slot >= maxSlots) return null;
    if (!batchSchedules.containsKey(batchId))
        return null; // TODO: throw new ResourceNotFoundException("Batch", batchId);
    return batchSchedules.get(batchId)[day][slot];
}
}

