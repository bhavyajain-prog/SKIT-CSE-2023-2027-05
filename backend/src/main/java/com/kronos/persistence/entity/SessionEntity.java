package com.kronos.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sessions")
public class SessionEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherEntity teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private BatchEntity batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_room_id")
    private RoomEntity preferredRoom;

    // Number of consecutive slots this session takes.
    @Column(nullable = false)
    private int slotDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id")
    private TimetableEntity timetable;
}
