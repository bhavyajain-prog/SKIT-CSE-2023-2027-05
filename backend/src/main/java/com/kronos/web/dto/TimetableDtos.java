package com.kronos.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class TimetableDtos {

    private TimetableDtos() {
    }

    /**
     * Settings for one generation run. Days run from Monday; slots are numbered
     * from 0. {@code lunchStart} is the slot no session may span, or null for
     * no lunch break.
     */
    public record GenerateRequest(
            @NotBlank @Size(max = 255) String name,
            @NotNull @Min(1) @Max(6) Integer weekDays,
            @NotNull @Min(1) @Max(16) Integer maxSlots,
            @Min(0) Integer lunchStart) {
    }

    public record TimetableSummary(
            Long id,
            String name,
            int weekDays,
            int maxSlots,
            Integer lunchStart,
            Instant createdAt,
            long scheduledCount,
            long unscheduledCount) {
    }

    public record TimetableDetail(
            TimetableSummary summary,
            List<SessionSlot> scheduled,
            List<SessionSlot> unscheduled) {
    }

    /**
     * One session of a timetable. {@code dayOfWeek} (0 = Monday), {@code day},
     * {@code startSlot} and the room fields are null when the session could
     * not be placed.
     */
    public record SessionSlot(
            Long sessionId,
            Integer dayOfWeek,
            String day,
            Integer startSlot,
            int slotDuration,
            Long subjectId,
            String subjectCode,
            String subjectName,
            Long teacherId,
            String teacherName,
            Long batchId,
            String batchName,
            Long roomId,
            String roomName) {
    }
}
