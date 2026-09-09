package com.kronos.engine.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Session {
    private long id;
    private Subject subject;
    private Teacher teacher;
    private Batch batch;
    private int slotDuration;
    private Room preferredRoom;
}