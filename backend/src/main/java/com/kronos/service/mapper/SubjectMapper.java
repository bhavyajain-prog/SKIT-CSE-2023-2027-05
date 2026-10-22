package com.kronos.service.mapper;

import com.kronos.engine.model.Subject;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.web.dto.SubjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface SubjectMapper {

    // 1. DB Entity <-> Engine Model
    @Mapping(target = "name", source = "subjectName")
    Subject toEngine(SubjectEntity entity);

    @Mapping(target = "subjectName", source = "name")
    @Mapping(target = "subjectCode", ignore = true)
    SubjectEntity toEntity(Subject pojo);

    // 2. DB Entity <-> Web DTO
    SubjectDto toDto(SubjectEntity entity);

    SubjectEntity toEntity(SubjectDto dto);

    // 3. Web DTO -> existing DB Entity (PUT update)
    @Mapping(target = "id", ignore = true)
    void updateEntity(SubjectDto dto, @MappingTarget SubjectEntity entity);
}
