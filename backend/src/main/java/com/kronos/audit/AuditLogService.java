package com.kronos.audit;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final JdbcTemplate jdbcTemplate;

    public void record(String entityName, Long entityId, AuditAction action, String snapshot) {
        jdbcTemplate.update(
                "INSERT INTO audit_log (entity_name, entity_id, action, snapshot, performed_by, performed_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                entityName,
                entityId,
                action.name(),
                snapshot,
                currentUser(),
                Instant.now());
    }

    private String currentUser() {
        return "system";
    }
}
