package com.kronos.service;

import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.repository.BatchRepository;
import com.kronos.persistence.repository.SubjectRepository;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.web.dto.BatchDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BatchServiceIntegrationTest {

    @Autowired
    private BatchService batchService;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private EntityManager entityManager;

    private Long os;
    private Long dbms;

    @BeforeEach
    void seedSubjects() {
        os = subjectRepository.save(subject("CS501")).getId();
        dbms = subjectRepository.save(subject("CS502")).getId();
    }

    @Test
    void createThenReadReturnsSubjectIds() {
        BatchDto created = batchService.createBatch(dto("CSE 3rd Year", List.of(os, dbms)));
        reload();

        BatchDto read = batchService.getBatchById(created.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(read.getName()).isEqualTo("CSE 3rd Year");
        assertThat(read.getSubjectIds()).containsExactlyInAnyOrder(os, dbms);
    }

    @Test
    void getAllBatchesListsCreatedBatches() {
        batchService.createBatch(dto("CSE 1st Year", List.of(os)));
        batchService.createBatch(dto("CSE 2nd Year", List.of(dbms)));

        assertThat(batchService.getAllBatches()).extracting(BatchDto::getName)
                .contains("CSE 1st Year", "CSE 2nd Year");
    }

    @Test
    void updateReplacesFieldsAndSubjects() {
        Long id = batchService.createBatch(dto("CSE 3rd Year", List.of(os))).getId();
        reload();

        BatchDto change = dto("CSE 3rd Year (Lateral)", List.of(dbms));
        change.setStrength(30);
        batchService.updateBatch(id, change);
        reload();

        BatchDto read = batchService.getBatchById(id);
        assertThat(read.getName()).isEqualTo("CSE 3rd Year (Lateral)");
        assertThat(read.getStrength()).isEqualTo(30);
        assertThat(read.getSubjectIds()).containsExactly(dbms);
    }

    @Test
    void deleteRemovesBatch() {
        Long id = batchService.createBatch(dto("CSE 4th Year", List.of(os))).getId();
        reload();

        batchService.deleteBatch(id);
        reload();

        assertThatThrownBy(() -> batchService.getBatchById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void missingBatchIsNotFound() {
        assertThatThrownBy(() -> batchService.getBatchById(-1L)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> batchService.updateBatch(-1L, dto("x", List.of())))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> batchService.deleteBatch(-1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void unknownSubjectIdIsRejectedByTheDatabase() {
        batchService.createBatch(dto("CSE 3rd Year", List.of(os, Long.MAX_VALUE)));

        assertThatThrownBy(() -> batchRepository.flush())
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // Flush pending writes and drop the persistence context so reads hit the database.
    private void reload() {
        entityManager.flush();
        entityManager.clear();
    }

    private static BatchDto dto(String name, List<Long> subjectIds) {
        BatchDto dto = new BatchDto();
        dto.setName(name);
        dto.setSection("A");
        dto.setStrength(60);
        dto.setSubjectIds(subjectIds);
        return dto;
    }

    private static SubjectEntity subject(String code) {
        return SubjectEntity.builder()
                .subjectCode(code)
                .subjectName("Subject " + code)
                .weight(3)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();
    }
}
