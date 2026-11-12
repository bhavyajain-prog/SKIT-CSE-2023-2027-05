package com.kronos.service.mapper;

import com.kronos.engine.model.Batch;
import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.web.dto.BatchDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BatchMapperTest {

    private final BatchMapper mapper = new BatchMapperImpl();

    @Test
    void toDtoMapsFieldsAndSubjectIds() {
        BatchEntity entity = batch(7L, List.of(subject(1L, "CS101"), subject(2L, "CS102")));

        BatchDto dto = mapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getName()).isEqualTo("CSE 3rd Year");
        assertThat(dto.getSection()).isEqualTo("A");
        assertThat(dto.getStrength()).isEqualTo(60);
        assertThat(dto.getSubjectIds()).containsExactly(1L, 2L);
    }

    @Test
    void toDtoKeepsMissingSubjectsAsNull() {
        assertThat(mapper.toDto(batch(7L, null)).getSubjectIds()).isNull();
    }

    @Test
    void toEntityTurnsSubjectIdsIntoReferences() {
        BatchDto dto = dto(99L, List.of(3L, 4L));

        BatchEntity entity = mapper.toEntity(dto);

        assertThat(entity.getId()).as("id is assigned by the database, not the client").isNull();
        assertThat(entity.getName()).isEqualTo("CSE 3rd Year");
        assertThat(entity.getSubjects()).extracting(SubjectEntity::getId).containsExactly(3L, 4L);
    }

    @Test
    void updateEntityOverwritesFieldsButKeepsId() {
        BatchEntity existing = batch(7L, new ArrayList<>(List.of(subject(1L, "CS101"))));
        BatchDto dto = dto(99L, List.of(5L));
        dto.setName("CSE 4th Year");
        dto.setStrength(55);

        mapper.updateEntity(dto, existing);

        assertThat(existing.getId()).isEqualTo(7L);
        assertThat(existing.getName()).isEqualTo("CSE 4th Year");
        assertThat(existing.getStrength()).isEqualTo(55);
        assertThat(existing.getSubjects()).extracting(SubjectEntity::getId).containsExactly(5L);
    }

    @Test
    void updateEntityWithoutSubjectIdsClearsSubjects() {
        BatchEntity existing = batch(7L, new ArrayList<>(List.of(subject(1L, "CS101"))));

        mapper.updateEntity(dto(null, null), existing);

        assertThat(existing.getSubjects()).isNull();
    }

    @Test
    void toEngineCarriesSubjectDetails() {
        BatchEntity entity = batch(7L, List.of(subject(1L, "CS101")));

        Batch engine = mapper.toEngine(entity);

        assertThat(engine.getId()).isEqualTo(7L);
        assertThat(engine.getStrength()).isEqualTo(60);
        assertThat(engine.getSubjects()).singleElement().satisfies(s -> {
            assertThat(s.getId()).isEqualTo(1L);
            assertThat(s.getName()).isEqualTo("Subject CS101");
            assertThat(s.getWeight()).isEqualTo(3);
            assertThat(s.getSlotDuration()).isEqualTo(2);
            assertThat(s.getSubjectType()).isEqualTo(SubjectType.LAB);
        });
    }

    @Test
    void nullInputsMapToNull() {
        assertThat(mapper.toDto(null)).isNull();
        assertThat(mapper.toEntity((BatchDto) null)).isNull();
        assertThat(mapper.toEngine((BatchEntity) null)).isNull();
    }

    private static BatchEntity batch(Long id, List<SubjectEntity> subjects) {
        BatchEntity entity = BatchEntity.builder()
                .name("CSE 3rd Year")
                .section("A")
                .strength(60)
                .subjects(subjects)
                .build();
        entity.setId(id);
        return entity;
    }

    private static SubjectEntity subject(Long id, String code) {
        SubjectEntity subject = SubjectEntity.builder()
                .subjectCode(code)
                .subjectName("Subject " + code)
                .weight(3)
                .slotDuration(2)
                .subjectType(SubjectType.LAB)
                .build();
        subject.setId(id);
        return subject;
    }

    private static BatchDto dto(Long id, List<Long> subjectIds) {
        BatchDto dto = new BatchDto();
        dto.setId(id);
        dto.setName("CSE 3rd Year");
        dto.setSection("A");
        dto.setStrength(60);
        dto.setSubjectIds(subjectIds);
        return dto;
    }
}
