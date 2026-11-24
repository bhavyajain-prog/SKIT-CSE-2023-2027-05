package com.kronos.persistence.repository;

import com.kronos.persistence.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    @Query("""
            select s from SessionEntity s
            join fetch s.subject
            join fetch s.teacher
            join fetch s.batch
            where s.timetable.id = :timetableId
            order by s.id
            """)
    List<SessionEntity> findDetailedByTimetableId(@Param("timetableId") Long timetableId);

    long countByTimetableId(Long timetableId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from SessionEntity s where s.timetable.id = :timetableId")
    void deleteByTimetableId(@Param("timetableId") Long timetableId);
}
