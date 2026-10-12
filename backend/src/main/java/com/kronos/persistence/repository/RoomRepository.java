package com.kronos.persistence.repository;

import com.kronos.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Optional<RoomEntity> findByNameIgnoreCase(String name);

    boolean existsByFixedBatchId(Long batchId);
}
