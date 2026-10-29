package com.kronos.service;

import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.repository.SessionRepository;
import com.kronos.persistence.repository.SubjectRepository;
import com.kronos.service.exception.ConflictException;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.service.mapper.SubjectMapper;
import com.kronos.web.dto.SubjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SessionRepository sessionRepository;
    private final SubjectMapper subjectMapper;

    @Transactional(readOnly = true)
    public Page<SubjectDto> getAllSubjects(Pageable pageable) {
        return subjectRepository.findAll(pageable)
                .map(subjectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectDto getSubjectById(Long id) {
        SubjectEntity entity = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
        return subjectMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public SubjectDto getSubjectByCode(String code) {
        String trimmedCode = code.trim();
        SubjectEntity entity = subjectRepository.findBySubjectCodeIgnoreCase(trimmedCode)
                .orElseThrow(() -> new ResourceNotFoundException("Subject code: " + trimmedCode));
        return subjectMapper.toDto(entity);
    }

    @Transactional
    public SubjectDto createSubject(SubjectDto dto) {
        String trimmedCode = dto.getSubjectCode().trim();
        if (subjectRepository.existsBySubjectCodeIgnoreCase(trimmedCode)) {
            throw new ConflictException("Subject with code '" + trimmedCode + "' already exists");
        }

        SubjectEntity entity = subjectMapper.toEntity(dto);
        entity.setSubjectCode(trimmedCode);
        entity.setSubjectName(dto.getSubjectName().trim());

        SubjectEntity saved = subjectRepository.save(entity);
        log.info("Created subject with id: {} and code: {}", saved.getId(), saved.getSubjectCode());
        return subjectMapper.toDto(saved);
    }

    @Transactional
    public SubjectDto updateSubject(Long id, SubjectDto dto) {
        SubjectEntity existing = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        String trimmedCode = dto.getSubjectCode().trim();
        if (subjectRepository.existsBySubjectCodeIgnoreCaseAndIdNot(trimmedCode, id)) {
            throw new ConflictException("Subject code '" + trimmedCode + "' is already in use by another subject");
        }

        subjectMapper.updateEntity(dto, existing);
        existing.setSubjectCode(trimmedCode);
        existing.setSubjectName(dto.getSubjectName().trim());

        SubjectEntity saved = subjectRepository.save(existing);
        log.info("Updated subject with id: {} and code: {}", saved.getId(), saved.getSubjectCode());
        return subjectMapper.toDto(saved);
    }

    @Transactional
    public void deleteSubject(Long id) {
        SubjectEntity entity = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        if (sessionRepository.existsBySubjectId(id)) {
            throw new ConflictException("Cannot delete subject: Currently referenced by active scheduled sessions");
        }

        subjectRepository.delete(entity);
        log.info("Deleted subject with id: {}", id);
    }
}
