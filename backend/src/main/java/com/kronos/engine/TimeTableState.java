package com.kronos.engine;

import com.kronos.engine.model.Session;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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
}


