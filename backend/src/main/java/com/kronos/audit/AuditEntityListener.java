package com.kronos.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;

public class AuditEntityListener {

    @PostPersist
    public void onPostPersist(Object entity) {
        record(entity, AuditAction.CREATE);
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        record(entity, AuditAction.UPDATE);
    }

    @PreRemove
    public void onPreRemove(Object entity) {
        record(entity, AuditAction.DELETE);
    }

    private void record(Object entity, AuditAction action) {
        AuditLogService auditLogService = SpringContextHolder.getBean(AuditLogService.class);
        auditLogService.record(entity.getClass().getSimpleName(), extractId(entity), action, serialize(entity));
    }

    private String serialize(Object entity) {
        try {
            ObjectMapper objectMapper = SpringContextHolder.getBean("auditObjectMapper", ObjectMapper.class);
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            return null;
        }
    }

    private Long extractId(Object entity) {
        Class<?> type = entity.getClass();

        while (type != null) {
            try {
                var field = type.getDeclaredField("id");
                field.setAccessible(true);

                Object value = field.get(entity);

                return value != null ? ((Number) value).longValue() : null;
            } catch (NoSuchFieldException e) {
                type = type.getSuperclass();
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        return null;
    }
}
