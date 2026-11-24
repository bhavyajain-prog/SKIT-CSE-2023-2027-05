package com.kronos.persistence.repository;

import com.kronos.persistence.entity.ScheduledSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledSessionRepository extends JpaRepository<ScheduledSessionEntity, Long> {

    @Query("""
            select ss from ScheduledSessionEntity ss
            join fetch ss.session s
            join fetch s.subject
            join fetch s.teacher
            join fetch s.batch
            join fetch ss.room
            where ss.timetable.id = :timetableId
            order by ss.dayOfWeek, ss.startSlot, ss.id
            """)
    List<ScheduledSessionEntity> findDetailedByTimetableId(@Param("timetableId") Long timetableId);

    long countByTimetableId(Long timetableId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from ScheduledSessionEntity ss where ss.timetable.id = :timetableId")
    void deleteByTimetableId(@Param("timetableId") Long timetableId);
}
