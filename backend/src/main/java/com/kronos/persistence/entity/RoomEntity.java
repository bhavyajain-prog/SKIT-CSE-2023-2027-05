package com.kronos.persistence.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@Data
public class RoomEntity extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_batch_id")
    private BatchEntity fixedBatch;
}
