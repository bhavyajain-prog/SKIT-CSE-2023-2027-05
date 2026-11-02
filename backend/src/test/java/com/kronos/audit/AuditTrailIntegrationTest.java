package com.kronos.audit;

import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuditTrailIntegrationTest {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    void createWritesCreateRowWithSnapshot() {
        SubjectEntity saved = subjectRepository.saveAndFlush(subject("CS301"));

        List<AuditLogEntity> rows = history(saved.getId());

        assertThat(rows).singleElement().satisfies(row -> {
            assertThat(row.getAction()).isEqualTo(AuditAction.CREATE);
            assertThat(row.getEntityName()).isEqualTo("SubjectEntity");
            assertThat(row.getPerformedBy()).isEqualTo("system");
            assertThat(row.getPerformedAt()).isNotNull();
            assertThat(row.getSnapshot())
                    .contains("\"subjectCode\":\"CS301\"")
                    .contains("\"subjectType\":\"LECTURE\"");
        });
    }

    @Test
    void updateWritesUpdateRowWithNewValues() {
        SubjectEntity saved = subjectRepository.saveAndFlush(subject("CS302"));

        saved.setSubjectName("Compilers");
        subjectRepository.saveAndFlush(saved);

        assertThat(history(saved.getId()))
                .extracting(AuditLogEntity::getAction)
                .containsExactlyInAnyOrder(AuditAction.CREATE, AuditAction.UPDATE);
        assertThat(history(saved.getId()))
                .filteredOn(row -> row.getAction() == AuditAction.UPDATE)
                .singleElement()
                .satisfies(row -> assertThat(row.getSnapshot()).contains("\"subjectName\":\"Compilers\""));
    }

    @Test
    void unchangedSaveWritesNoUpdateRow() {
        SubjectEntity saved = subjectRepository.saveAndFlush(subject("CS303"));

        subjectRepository.saveAndFlush(saved);

        assertThat(history(saved.getId()))
                .extracting(AuditLogEntity::getAction)
                .containsExactly(AuditAction.CREATE);
    }

    @Test
    void deleteWritesDeleteRow() {
        SubjectEntity saved = subjectRepository.saveAndFlush(subject("CS304"));
        Long id = saved.getId();

        subjectRepository.delete(saved);
        subjectRepository.flush();

        assertThat(history(id))
                .extracting(AuditLogEntity::getAction)
                .containsExactlyInAnyOrder(AuditAction.CREATE, AuditAction.DELETE);
    }

    private List<AuditLogEntity> history(Long id) {
        return auditLogRepository.findByEntityNameAndEntityIdOrderByPerformedAtDesc("SubjectEntity", id);
    }

    private static SubjectEntity subject(String code) {
        return SubjectEntity.builder()
                .subjectCode(code)
                .subjectName("Operating Systems")
                .weight(4)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();
    }
}
