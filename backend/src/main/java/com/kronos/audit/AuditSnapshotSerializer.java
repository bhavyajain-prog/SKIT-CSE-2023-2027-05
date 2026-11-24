package com.kronos.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

/**
 * Serializes entities to JSON for audit snapshots.
 * <p>
 * The ObjectMapper is kept private rather than exposed as a bean: any
 * ObjectMapper bean makes Spring Boot skip its own HTTP ObjectMapper, and this
 * one would not match Boot's HTTP settings, which broke every API error body.
 */
@Component
public class AuditSnapshotSerializer {

    private final ObjectMapper objectMapper;

    public AuditSnapshotSerializer() {
        Hibernate6Module hibernateModule = new Hibernate6Module();
        hibernateModule.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(hibernateModule);
        // Entities such as TimetableEntity hold java.time values; without this
        // module their snapshots fail to serialize and are stored as null.
        objectMapper.registerModule(new JavaTimeModule());
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
