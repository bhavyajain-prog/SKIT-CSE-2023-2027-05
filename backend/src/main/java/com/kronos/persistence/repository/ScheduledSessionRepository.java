package com.kronos.persistence.repository;

import com.kronos.persistence.entity.ScheduledSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledSessionRepository extends JpaRepository<ScheduledSessionEntity, Long> {
}
