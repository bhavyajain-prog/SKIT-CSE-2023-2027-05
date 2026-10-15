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
@Table(name = "scheduled_sessions")
public class ScheduledSessionEntity extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    private SessionEntity session;

    @Column(nullable = false)
    private int dayOfWeek;

    @Column(nullable = false)
    private int startSlot;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private RoomEntity room;
}
