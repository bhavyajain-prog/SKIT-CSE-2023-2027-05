package com.kronos.engine.constraints;

import com.kronos.engine.TimeTableState;
import com.kronos.engine.model.Room;
import com.kronos.engine.model.Session;

public interface Constraints {
    int evaluate(Session session, Room room, int day, int start, TimeTableState state);

}
