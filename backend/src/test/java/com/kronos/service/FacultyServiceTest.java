package com.kronos.service;

import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.TeacherEntity;
import com.kronos.persistence.repository.SubjectRepository;
import com.kronos.persistence.repository.TeacherRepository;
import com.kronos.service.exception.BadRequestException;
import com.kronos.service.exception.ConflictException;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.service.mapper.FacultyMapper;
import com.kronos.web.dto.FacultyDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private FacultyMapper facultyMapper;

    @InjectMocks
    private FacultyService facultyService;

    private SubjectEntity subject(Long id) {
        SubjectEntity subject = new SubjectEntity();
        subject.setId(id);
        return subject;
    }

    private FacultyDto dto(String email, Long... subjectIds) {
        FacultyDto dto = new FacultyDto();
        dto.setFirstName("Asha");
        dto.setEmail(email);
        dto.setSubjectIds(List.of(subjectIds));
        return dto;
    }

    @Test
    void getAllFacultyMapsEveryRow() {
        TeacherEntity a = new TeacherEntity();
        TeacherEntity b = new TeacherEntity();
        FacultyDto dtoA = new FacultyDto();
        FacultyDto dtoB = new FacultyDto();
        when(teacherRepository.findAll()).thenReturn(List.of(a, b));
        when(facultyMapper.toDto(a)).thenReturn(dtoA);
        when(facultyMapper.toDto(b)).thenReturn(dtoB);

        assertThat(facultyService.getAllFaculty()).containsExactly(dtoA, dtoB);
    }

    @Test
    void getFacultyByIdThrowsWhenMissing() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultyService.getFacultyById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Faculty not found with id: 1");
    }

    @Test
    void createFacultyRejectsDuplicateEmail() {
        when(teacherRepository.existsByEmailIgnoreCase("asha@kronos.test")).thenReturn(true);

        assertThatThrownBy(() -> facultyService.createFaculty(dto("asha@kronos.test")))
                .isInstanceOf(ConflictException.class);

        verify(teacherRepository, never()).save(any());
    }

    @Test
    void createFacultySavesAndLinksSubjects() {
        FacultyDto input = dto("asha@kronos.test", 1L, 2L);
        TeacherEntity entity = new TeacherEntity();
        FacultyDto result = new FacultyDto();
        when(subjectRepository.findAllById(any())).thenReturn(List.of(subject(1L), subject(2L)));
        when(facultyMapper.toEntity(input)).thenReturn(entity);
        when(teacherRepository.save(entity)).thenReturn(entity);
        when(facultyMapper.toDto(entity)).thenReturn(result);

        assertThat(facultyService.createFaculty(input)).isSameAs(result);
        assertThat(entity.getSubjects()).hasSize(2);
    }

    @Test
    void createFacultyRejectsUnknownSubject() {
        when(subjectRepository.findAllById(any())).thenReturn(List.of(subject(1L)));

        assertThatThrownBy(() -> facultyService.createFaculty(dto("asha@kronos.test", 1L, 2L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Subject not found with id: 2");

        verify(teacherRepository, never()).save(any());
    }

    @Test
    void updateFacultyRejectsEmailUsedByAnotherTeacher() {
        when(teacherRepository.findById(5L)).thenReturn(Optional.of(new TeacherEntity()));
        when(teacherRepository.existsByEmailIgnoreCaseAndIdNot("asha@kronos.test", 5L)).thenReturn(true);

        assertThatThrownBy(() -> facultyService.updateFaculty(5L, dto("asha@kronos.test")))
                .isInstanceOf(ConflictException.class);

        verify(teacherRepository, never()).save(any());
    }

    @Test
    void deleteFacultyThrowsWhenMissing() {
        when(teacherRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultyService.deleteFaculty(9L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(teacherRepository, never()).delete(any());
    }

    @Test
    void deleteFacultyTurnsReferenceViolationIntoConflict() {
        when(teacherRepository.findById(3L)).thenReturn(Optional.of(new TeacherEntity()));
        doThrow(new DataIntegrityViolationException("still referenced")).when(teacherRepository).flush();

        assertThatThrownBy(() -> facultyService.deleteFaculty(3L))
                .isInstanceOf(ConflictException.class);
    }
}
