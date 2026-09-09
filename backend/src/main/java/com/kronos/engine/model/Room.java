package com.kronos.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Room {
    private long id;
    private String name;
    private int capacity;
    private Batch fixedBatch;

    public Room(Room orig, Batch fixedBatch) {
        this.id = orig.getId();
        this.name = orig.getName();
        this.capacity = orig.getCapacity();
        this.fixedBatch = fixedBatch;
    }
}
