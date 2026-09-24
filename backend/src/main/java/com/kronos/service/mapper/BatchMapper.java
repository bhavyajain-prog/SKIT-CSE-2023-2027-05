package com.kronos.service.mapper;

import com.kronos.engine.model.Batch;
import com.kronos.persistence.model.BatchEntity;
import com.kronos.persistence.model.SubjectEntity;
import org.mapstruct.Mapping;

import java.util.List;

public interface BatchMapper {
    // 1. DB <-> Engine POJO (Used by your TimetableService / Engine)
    Batch toEngine(BatchEntity entity);

    BatchEntity toEntity(Batch pojo);

    // 2. DB <-> Web DTO (Used by your BatchService / REST Controller)
    @Mapping(target = "subjectIds", source = "subjects")
    BatchDto toDto(BatchEntity entity);

    @Mapping(target = "subjects", source = "subjectIds")
    BatchEntity toEntity(BatchDto dto);

    // 3. MapStruct Helpers for flattening/nesting the Subject IDs
    default List<Long> mapSubjectsToIds(List<SubjectEntity> subjects) {
        if (subjects == null) {
            return null;
        }
        return subjects.stream().map(SubjectEntity::getId).toList();
    }

    default List<SubjectEntity> mapIdsToSubjects(List<Long> subjectIds) {
        if (subjectIds == null) {
            return null;
        }
        return subjectIds.stream().map(id -> {
            SubjectEntity subject = new SubjectEntity();
            subject.setId(id);
            return subject;
        }).toList();
    }
}
