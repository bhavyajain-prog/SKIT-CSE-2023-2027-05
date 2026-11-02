package com.kronos.persistence;

import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.RoomEntity;
import com.kronos.persistence.entity.ScheduledSessionEntity;
import com.kronos.persistence.entity.SessionEntity;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.TeacherEntity;
import com.kronos.persistence.repository.BatchRepository;
import com.kronos.persistence.repository.RoomRepository;
import com.kronos.persistence.repository.ScheduledSessionRepository;
import com.kronos.persistence.repository.SessionRepository;
import com.kronos.persistence.repository.SubjectRepository;
import com.kronos.persistence.repository.TeacherRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Round-trips every entity through the Flyway schema and checks the database
 * constraints that the API relies on for its 409 responses.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EntityPersistenceIntegrationTest {

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private ScheduledSessionRepository scheduledSessionRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void subjectTypeIsStoredAsItsName() {
        SubjectEntity saved = subjectRepository.saveAndFlush(subject("CS401", SubjectType.LAB));

        String stored = jdbcTemplate.queryForObject(
                "SELECT subject_type FROM subjects WHERE id = ?", String.class, saved.getId());

        assertThat(stored).isEqualTo("LAB");
    }

    @Test
    void fullScheduleGraphRoundTrips() {
        SubjectEntity subject = subjectRepository.save(subject("CS402", SubjectType.LECTURE));
        TeacherEntity teacher = teacherRepository.save(TeacherEntity.builder()
                .firstName("Asha").lastName("Verma").email("asha@example.edu")
                .subjects(List.of(subject)).build());
        BatchEntity batch = batchRepository.save(BatchEntity.builder()
                .name("CSE 3rd Year").section("A").strength(60)
                .subjects(List.of(subject)).build());
        RoomEntity room = roomRepository.save(RoomEntity.builder()
                .name("LT-1").capacity(80).fixedBatch(batch).build());
        SessionEntity session = sessionRepository.save(SessionEntity.builder()
                .subject(subject).teacher(teacher).batch(batch).preferredRoom(room).build());
        ScheduledSessionEntity scheduled = scheduledSessionRepository.save(ScheduledSessionEntity.builder()
                .session(session).dayOfWeek(1).startSlot(3).room(room).build());
        entityManager.flush();
        entityManager.clear();

        ScheduledSessionEntity loaded = scheduledSessionRepository.findById(scheduled.getId()).orElseThrow();

        assertThat(loaded.getDayOfWeek()).isEqualTo(1);
        assertThat(loaded.getStartSlot()).isEqualTo(3);
        assertThat(loaded.getRoom().getName()).isEqualTo("LT-1");
        assertThat(loaded.getRoom().getFixedBatch().getId()).isEqualTo(batch.getId());
        SessionEntity loadedSession = loaded.getSession();
        assertThat(loadedSession.getSubject().getSubjectCode()).isEqualTo("CS402");
        assertThat(loadedSession.getTeacher().getEmail()).isEqualTo("asha@example.edu");
        assertThat(loadedSession.getTeacher().getSubjects()).extracting(SubjectEntity::getId)
                .containsExactly(subject.getId());
        assertThat(loadedSession.getBatch().getSubjects()).extracting(SubjectEntity::getId)
                .containsExactly(subject.getId());
        assertThat(loadedSession.getPreferredRoom().getId()).isEqualTo(room.getId());
    }

    @Test
    void duplicateSubjectCodeIsRejected() {
        subjectRepository.saveAndFlush(subject("CS403", SubjectType.LECTURE));

        assertThatThrownBy(() -> subjectRepository.saveAndFlush(subject("CS403", SubjectType.LAB)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void duplicateTeacherEmailIsRejected() {
        teacherRepository.saveAndFlush(teacher("dup@example.edu"));

        assertThatThrownBy(() -> teacherRepository.saveAndFlush(teacher("dup@example.edu")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void duplicateRoomNameIsRejected() {
        roomRepository.saveAndFlush(RoomEntity.builder().name("LAB-2").capacity(30).build());

        assertThatThrownBy(() -> roomRepository.saveAndFlush(RoomEntity.builder().name("LAB-2").capacity(40).build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deletingSubjectStillUsedByBatchIsRejected() {
        SubjectEntity subject = subjectRepository.save(subject("CS404", SubjectType.LECTURE));
        batchRepository.saveAndFlush(BatchEntity.builder()
                .name("CSE 2nd Year").section("B").strength(50)
                .subjects(List.of(subject)).build());
        // Start from an empty persistence context, like a fresh DELETE request.
        entityManager.clear();

        subjectRepository.deleteById(subject.getId());

        assertThatThrownBy(() -> subjectRepository.flush())
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private static SubjectEntity subject(String code, SubjectType type) {
        return SubjectEntity.builder()
                .subjectCode(code)
                .subjectName("Subject " + code)
                .weight(3)
                .slotDuration(1)
                .subjectType(type)
                .build();
    }

    private static TeacherEntity teacher(String email) {
        return TeacherEntity.builder().firstName("Ravi").email(email).build();
    }
}
