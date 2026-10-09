package com.kronos.engine.algorithm;

import com.kronos.engine.model.ScheduledResult;
import com.kronos.engine.model.ScheduledSession;
import com.kronos.engine.model.Session;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlacementAlgo implements Algo{
    protected final List<Session> sessions;
    protected final List<ScheduledSession> lockedSessions;

    public AbstractPlacementAlgo(List<Session> sessions, List<ScheduledSession> lockedSessions) {
        this.sessions = sessions;
        this.lockedSessions = lockedSessions;
    }

    @Override
    public final ScheduledResult generateTimeTable() {
        sortSessionsByDifficulty();

        List<ScheduledSession> scheduledSessions = new ArrayList<>();
        List<Session> unscheduledSessions = new ArrayList<>();

        for (Session session : sessions) {
            boolean isPlaced = attemptPlacing(session, scheduledSessions);
            if (!isPlaced) {
                unscheduledSessions.add(session);
            }
        }

        return ScheduledResult.builder()
                .sessions(scheduledSessions)
                .unscheduledSessions(unscheduledSessions)
                .build();
    }

    protected abstract void sortSessionsByDifficulty();
    protected abstract boolean attemptPlacing(Session session, List<ScheduledSession> scheduledSessions);

}
