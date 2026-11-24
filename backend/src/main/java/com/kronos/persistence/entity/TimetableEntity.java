package com.kronos.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * One timetable generation run, with the grid settings it was generated for.
 * Its sessions and placements point back to it through {@code timetable_id}.
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timetables")
public class TimetableEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int weekDays;

    @Column(nullable = false)
    private int maxSlots;

    // Null when the day has no lunch break.
    private Integer lunchStart;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
