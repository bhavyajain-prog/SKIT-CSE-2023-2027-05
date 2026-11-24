package com.kronos.persistence.repository;

import com.kronos.persistence.entity.TimetableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableEntity, Long> {
    List<TimetableEntity> findAllByOrderByCreatedAtDesc();
}
