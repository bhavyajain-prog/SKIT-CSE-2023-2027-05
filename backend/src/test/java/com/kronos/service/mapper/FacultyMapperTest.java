package com.kronos.service.mapper;

import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.TeacherEntity;
import com.kronos.web.dto.FacultyDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FacultyMapperTest {

    private final FacultyMapper mapper = new FacultyMapperImpl();

    private SubjectEntity subject(Long id) {
        SubjectEntity subject = new SubjectEntity();
        subject.setId(id);
        return subject;
    }

    @Test
    void toDtoMapsFieldsAndSubjectIds() {
        TeacherEntity teacher = TeacherEntity.builder()
                .firstName("Asha")
                .lastName("Rao")
                .email("asha@kronos.test")
                .subjects(List.of(subject(3L), subject(4L)))
                .build();
        teacher.setId(10L);

        FacultyDto dto = mapper.toDto(teacher);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getFirstName()).isEqualTo("Asha");
        assertThat(dto.getLastName()).isEqualTo("Rao");
        assertThat(dto.getEmail()).isEqualTo("asha@kronos.test");
        assertThat(dto.getSubjectIds()).containsExactly(3L, 4L);
    }

    @Test
    void toDtoGivesEmptySubjectIdsWhenTeacherHasNoSubjects() {
        TeacherEntity teacher = TeacherEntity.builder().firstName("Asha").email("asha@kronos.test").build();

        assertThat(mapper.toDto(teacher).getSubjectIds()).isEmpty();
    }

    @Test
    void toEntityCopiesFieldsAndLeavesSubjectsForTheService() {
        FacultyDto dto = new FacultyDto();
        dto.setFirstName("Meera");
        dto.setLastName("Nair");
        dto.setEmail("meera@kronos.test");
        dto.setSubjectIds(List.of(1L));

        TeacherEntity entity = mapper.toEntity(dto);

        assertThat(entity.getFirstName()).isEqualTo("Meera");
        assertThat(entity.getLastName()).isEqualTo("Nair");
        assertThat(entity.getEmail()).isEqualTo("meera@kronos.test");
        assertThat(entity.getSubjects()).isNull();
    }

    @Test
    void updateEntityKeepsIdAndSubjects() {
        SubjectEntity kept = subject(5L);
        TeacherEntity existing = TeacherEntity.builder()
                .firstName("Old")
                .email("old@kronos.test")
                .subjects(List.of(kept))
                .build();
        existing.setId(7L);
        FacultyDto dto = new FacultyDto();
        dto.setId(99L);
        dto.setFirstName("New");
        dto.setEmail("new@kronos.test");

        mapper.updateEntity(dto, existing);

        assertThat(existing.getId()).isEqualTo(7L);
        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getEmail()).isEqualTo("new@kronos.test");
        assertThat(existing.getSubjects()).containsExactly(kept);
    }
}
