package com.kronos.engine.model;

import com.kronos.enums.WeekDay;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduledSession {
    private Session session;
    private WeekDay weekDay;
    private int startSlot;
    private Room assignedRoom;
}
