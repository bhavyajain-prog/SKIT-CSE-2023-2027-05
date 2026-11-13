package com.kronos.service.mapper;

import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.TeacherEntity;
import com.kronos.web.dto.FacultyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = CentralMapperConfig.class)
public interface FacultyMapper {

    @Mapping(target = "subjectIds", source = "subjects", qualifiedByName = "subjectsToIds")
    FacultyDto toDto(TeacherEntity entity);

    @Mapping(target = "subjects", ignore = true)
    TeacherEntity toEntity(FacultyDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subjects", ignore = true)
    void updateEntity(FacultyDto dto, @MappingTarget TeacherEntity entity);

    @Named("subjectsToIds")
    default List<Long> subjectsToIds(List<SubjectEntity> subjects) {
        if (subjects == null) {
            return new ArrayList<>();
        }
        return subjects.stream().map(SubjectEntity::getId).toList();
    }
}
