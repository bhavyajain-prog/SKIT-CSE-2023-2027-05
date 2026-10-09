package com.kronos.engine.algorithm;

import com.kronos.engine.model.ScheduledResult;

@FunctionalInterface
public interface Algo {
    ScheduledResult generateTimeTable();
}
