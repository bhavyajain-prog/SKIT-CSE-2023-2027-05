package com.kronos.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.stereotype.Component;

/**
 * Serializes entities to JSON for audit snapshots.
 * <p>
 * The ObjectMapper is kept private rather than exposed as a bean: any
 * ObjectMapper bean makes Spring Boot skip its own HTTP ObjectMapper, and this
 * one cannot write {@code java.time} types, which broke every API error body.
 */
@Component
public class AuditSnapshotSerializer {

    private final ObjectMapper objectMapper;

    public AuditSnapshotSerializer() {
        Hibernate6Module hibernateModule = new Hibernate6Module();
        hibernateModule.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(hibernateModule);
    }

    /**
     * Returns the entity as JSON, or {@code null} if it cannot be serialized.
     */
    public String serialize(Object entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            return null;
        }
    }
}
