package com.kronos.persistence.repository;

import com.kronos.engine.model.ScheduledSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledSessionRepository extends JpaRepository<ScheduledSession, Long> {
}
