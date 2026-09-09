package com.kronos.engine.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ScheduledResult {
    private List<ScheduledSession> sessions;
    private List<Session> unscheduledSessions;
}
