package com.kronos.service;

import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.repository.SessionRepository;
import com.kronos.persistence.repository.SubjectRepository;
import com.kronos.service.exception.ConflictException;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.service.mapper.SubjectMapper;
import com.kronos.web.dto.SubjectDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private SubjectService subjectService;

    private SubjectEntity sampleEntity;
    private SubjectDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleEntity = SubjectEntity.builder()
                .subjectCode("5CS4-01")
                .subjectName("Operating Systems")
                .weight(3)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();
        sampleEntity.setId(1L);

        sampleDto = SubjectDto.builder()
                .id(1L)
                .subjectCode("5CS4-01")
                .subjectName("Operating Systems")
                .weight(3)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();
    }

    @Test
    @DisplayName("getAllSubjects with Pageable returns paged DTOs")
    void getAllSubjects_WithPageable_ReturnsPagedDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubjectEntity> entityPage = new PageImpl<>(List.of(sampleEntity), pageable, 1);
        when(subjectRepository.findAll(pageable)).thenReturn(entityPage);
        when(subjectMapper.toDto(sampleEntity)).thenReturn(sampleDto);

        Page<SubjectDto> result = subjectService.getAllSubjects(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getSubjectCode()).isEqualTo("5CS4-01");
        verify(subjectRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getAllSubjects unpaged returns all DTOs")
    void getAllSubjects_Unpaged_ReturnsAllDtos() {
        when(subjectRepository.findAll()).thenReturn(List.of(sampleEntity));
        when(subjectMapper.toDto(sampleEntity)).thenReturn(sampleDto);

        List<SubjectDto> result = subjectService.getAllSubjects();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getSubjectName()).isEqualTo("Operating Systems");
        verify(subjectRepository).findAll();
    }

    @Test
    @DisplayName("getSubjectById when found returns mapped DTO")
    void getSubjectById_WhenFound_ReturnsDto() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(subjectMapper.toDto(sampleEntity)).thenReturn(sampleDto);

        SubjectDto result = subjectService.getSubjectById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubjectCode()).isEqualTo("5CS4-01");
        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("getSubjectById when not found throws ResourceNotFoundException")
    void getSubjectById_WhenNotFound_ThrowsResourceNotFoundException() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.getSubjectById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
        verify(subjectRepository).findById(99L);
    }

    @Test
    @DisplayName("getSubjectByCode when found returns mapped DTO")
    void getSubjectByCode_WhenFound_ReturnsDto() {
        when(subjectRepository.findBySubjectCodeIgnoreCase("5CS4-01")).thenReturn(Optional.of(sampleEntity));
        when(subjectMapper.toDto(sampleEntity)).thenReturn(sampleDto);

        SubjectDto result = subjectService.getSubjectByCode("5CS4-01");

        assertThat(result).isNotNull();
        assertThat(result.getSubjectCode()).isEqualTo("5CS4-01");
        verify(subjectRepository).findBySubjectCodeIgnoreCase("5CS4-01");
    }

    @Test
    @DisplayName("getSubjectByCode when not found throws ResourceNotFoundException")
    void getSubjectByCode_WhenNotFound_ThrowsResourceNotFoundException() {
        when(subjectRepository.findBySubjectCodeIgnoreCase("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.getSubjectByCode("UNKNOWN"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("UNKNOWN");
    }

    @Test
    @DisplayName("createSubject when valid saves and returns DTO")
    void createSubject_WhenValid_SavesAndReturnsDto() {
        SubjectDto inputDto = SubjectDto.builder()
                .subjectCode("5CS4-02")
                .subjectName("Compiler Design")
                .weight(4)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();

        SubjectEntity toSave = SubjectEntity.builder()
                .subjectCode("5CS4-02")
                .subjectName("Compiler Design")
                .weight(4)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();

        SubjectEntity saved = SubjectEntity.builder()
                .subjectCode("5CS4-02")
                .subjectName("Compiler Design")
                .weight(4)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();
        saved.setId(2L);

        SubjectDto expectedDto = SubjectDto.builder()
                .id(2L)
                .subjectCode("5CS4-02")
                .subjectName("Compiler Design")
                .weight(4)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();

        when(subjectRepository.existsBySubjectCodeIgnoreCase("5CS4-02")).thenReturn(false);
        when(subjectMapper.toEntity(inputDto)).thenReturn(toSave);
        when(subjectRepository.save(any(SubjectEntity.class))).thenReturn(saved);
        when(subjectMapper.toDto(saved)).thenReturn(expectedDto);

        SubjectDto result = subjectService.createSubject(inputDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getSubjectCode()).isEqualTo("5CS4-02");
        verify(subjectRepository).save(any(SubjectEntity.class));
    }

    @Test
    @DisplayName("createSubject when duplicate code throws ConflictException")
    void createSubject_WhenDuplicateCode_ThrowsConflictException() {
        when(subjectRepository.existsBySubjectCodeIgnoreCase("5CS4-01")).thenReturn(true);

        assertThatThrownBy(() -> subjectService.createSubject(sampleDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("5CS4-01");
        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateSubject when valid updates and returns DTO")
    void updateSubject_WhenValid_UpdatesAndReturnsDto() {
        SubjectDto updateDto = SubjectDto.builder()
                .subjectCode("5CS4-01-REV")
                .subjectName("Operating Systems Advanced")
                .weight(4)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(subjectRepository.existsBySubjectCodeIgnoreCaseAndIdNot("5CS4-01-REV", 1L)).thenReturn(false);
        when(subjectRepository.save(sampleEntity)).thenReturn(sampleEntity);
        when(subjectMapper.toDto(sampleEntity)).thenReturn(updateDto);

        SubjectDto result = subjectService.updateSubject(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getSubjectCode()).isEqualTo("5CS4-01-REV");
        verify(subjectMapper).updateEntity(updateDto, sampleEntity);
        verify(subjectRepository).save(sampleEntity);
    }

    @Test
    @DisplayName("updateSubject when not found throws ResourceNotFoundException")
    void updateSubject_WhenNotFound_ThrowsResourceNotFoundException() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.updateSubject(99L, sampleDto))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateSubject when duplicate code on another entity throws ConflictException")
    void updateSubject_WhenDuplicateCode_ThrowsConflictException() {
        SubjectDto updateDto = SubjectDto.builder()
                .subjectCode("EXISTING-CODE")
                .subjectName("Test")
                .weight(3)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(subjectRepository.existsBySubjectCodeIgnoreCaseAndIdNot("EXISTING-CODE", 1L)).thenReturn(true);

        assertThatThrownBy(() -> subjectService.updateSubject(1L, updateDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("EXISTING-CODE");
        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteSubject when valid deletes entity")
    void deleteSubject_WhenValid_DeletesEntity() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(sessionRepository.existsBySubjectId(1L)).thenReturn(false);

        subjectService.deleteSubject(1L);

        verify(subjectRepository).delete(sampleEntity);
    }

    @Test
    @DisplayName("deleteSubject when not found throws ResourceNotFoundException")
    void deleteSubject_WhenNotFound_ThrowsResourceNotFoundException() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.deleteSubject(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(subjectRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteSubject when referenced by active sessions throws ConflictException")
    void deleteSubject_WhenReferencedBySessions_ThrowsConflictException() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(sessionRepository.existsBySubjectId(1L)).thenReturn(true);

        assertThatThrownBy(() -> subjectService.deleteSubject(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("active scheduled sessions");
        verify(subjectRepository, never()).delete(any());
    }
}
