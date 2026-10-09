package com.kronos.audit;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    List<AuditLogEntity> findByEntityNameAndEntityIdOrderByPerformedAtDesc(String entityName, Long entityId);
}
