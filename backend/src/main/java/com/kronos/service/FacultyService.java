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
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacultyService {
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final FacultyMapper facultyMapper;

    @Transactional(readOnly = true)
    public List<FacultyDto> getAllFaculty() {
        return teacherRepository.findAll().stream()
                .map(facultyMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public FacultyDto getFacultyById(Long id) {
        return facultyMapper.toDto(findOrThrow(id));
    }

    @Transactional
    public FacultyDto createFaculty(FacultyDto dto) {
        if (teacherRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ConflictException("Faculty email already exists: " + dto.getEmail());
        }
        List<SubjectEntity> subjects = resolveSubjects(dto.getSubjectIds());
        TeacherEntity entity = facultyMapper.toEntity(dto);
        entity.setSubjects(subjects);
        return facultyMapper.toDto(teacherRepository.save(entity));
    }

    @Transactional
    public FacultyDto updateFaculty(Long id, FacultyDto dto) {
        TeacherEntity existing = findOrThrow(id);
        if (teacherRepository.existsByEmailIgnoreCaseAndIdNot(dto.getEmail(), id)) {
            throw new ConflictException("Faculty email already exists: " + dto.getEmail());
        }
        List<SubjectEntity> subjects = resolveSubjects(dto.getSubjectIds());
        facultyMapper.updateEntity(dto, existing);
        existing.setSubjects(subjects);
        return facultyMapper.toDto(teacherRepository.save(existing));
    }

    @Transactional
    public void deleteFaculty(Long id) {
        TeacherEntity existing = findOrThrow(id);
        try {
            teacherRepository.delete(existing);
            teacherRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Faculty is still referenced and cannot be deleted");
        }
    }

    private TeacherEntity findOrThrow(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", id));
    }

    private List<SubjectEntity> resolveSubjects(List<Long> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> wanted = new LinkedHashSet<>(subjectIds);
        List<SubjectEntity> found = subjectRepository.findAllById(wanted);
        if (found.size() != wanted.size()) {
            Set<Long> foundIds = found.stream().map(SubjectEntity::getId).collect(Collectors.toSet());
            Long missing = wanted.stream().filter(id -> !foundIds.contains(id)).findFirst().orElseThrow();
            throw new BadRequestException("Subject not found with id: " + missing);
        }
        return new ArrayList<>(found);
    }
}
