package com.kronos.service.mapper;

import com.kronos.engine.model.Batch;
import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.web.dto.BatchDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    // 1. DB Entity <-> Engine POJO
    Batch toEngine(BatchEntity entity);

    BatchEntity toEntity(Batch pojo);

    // 2. DB Entity -> Web DTO
    @Mapping(
            target = "subjectIds",
            source = "subjects",
            qualifiedByName = "mapSubjectsToIds"
    )
    BatchDto toDto(BatchEntity entity);

    // 3. Web DTO -> DB Entity
    @Mapping(
            target = "subjects",
            source = "subjectIds",
            qualifiedByName = "mapIdsToSubjects"
    )
    BatchEntity toEntity(BatchDto dto);

    // 4. Convert SubjectEntity list to subject ID list
    @Named("mapSubjectsToIds")
    default List<Long> mapSubjectsToIds(
            List<SubjectEntity> subjects) {

        if (subjects == null) {
            return null;
        }

        return subjects.stream()
                .map(SubjectEntity::getId)
                .toList();
    }

    // 5. Convert subject ID list to SubjectEntity list
    @Named("mapIdsToSubjects")
    default List<SubjectEntity> mapIdsToSubjects(
            List<Long> subjectIds) {

        if (subjectIds == null) {
            return null;
        }

        return subjectIds.stream()
                .map(id -> {
                    SubjectEntity subject = new SubjectEntity();
                    subject.setId(id);
                    return subject;
                })
                .toList();
    }
}