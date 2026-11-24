package com.kronos.service.mapper;

import com.kronos.engine.model.Batch;
import com.kronos.engine.model.Room;
import com.kronos.engine.model.Subject;
import com.kronos.engine.model.Teacher;
import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.RoomEntity;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.TeacherEntity;
import com.kronos.service.mapper.EngineModelMapper.EngineInput;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kronos.testsupport.MasterDataFixtures.batch;
import static com.kronos.testsupport.MasterDataFixtures.room;
import static com.kronos.testsupport.MasterDataFixtures.subject;
import static com.kronos.testsupport.MasterDataFixtures.teacher;
import static org.assertj.core.api.Assertions.assertThat;

class EngineModelMapperTest {

    private final EngineModelMapper mapper = new EngineModelMapper();

    @Test
    void mapsFieldsOfEveryEntity() {
        SubjectEntity maths = subject(1L, "MA101", 3, 1, SubjectType.LECTURE);
        TeacherEntity teacher = teacher(10L, "Asha", List.of(maths));
        BatchEntity batch = batch(20L, 60, List.of(maths));
        RoomEntity room = room(30L, "LT-1", 80, null);

        EngineInput input = mapper.toEngine(List.of(teacher), List.of(batch), List.of(room));

        Teacher engineTeacher = input.teachers().getFirst();
        assertThat(engineTeacher.getId()).isEqualTo(10L);
        assertThat(engineTeacher.getFirstName()).isEqualTo("Asha");
        assertThat(engineTeacher.getEmail()).isEqualTo("asha@kronos.test");

        Subject engineSubject = engineTeacher.getSubjects().getFirst();
        assertThat(engineSubject.getId()).isEqualTo(1L);
        assertThat(engineSubject.getName()).isEqualTo("Maths");
        assertThat(engineSubject.getWeight()).isEqualTo(3);
        assertThat(engineSubject.getSlotDuration()).isEqualTo(1);
        assertThat(engineSubject.getSubjectType()).isEqualTo(SubjectType.LECTURE);

        Batch engineBatch = input.batches().getFirst();
        assertThat(engineBatch.getName()).isEqualTo("CSE");
        assertThat(engineBatch.getSection()).isEqualTo("A");
        assertThat(engineBatch.getStrength()).isEqualTo(60);

        Room engineRoom = input.rooms().getFirst();
        assertThat(engineRoom.getName()).isEqualTo("LT-1");
        assertThat(engineRoom.getCapacity()).isEqualTo(80);
        assertThat(engineRoom.getFixedBatch()).isNull();
    }

    @Test
    void subjectSharedByTeacherAndBatchMapsToOneInstance() {
        SubjectEntity maths = subject(1L, "MA101", 3, 1, SubjectType.LECTURE);

        EngineInput input = mapper.toEngine(
                List.of(teacher(10L, "Asha", List.of(maths))),
                List.of(batch(20L, 60, List.of(maths))),
                List.of());

        assertThat(input.teachers().getFirst().getSubjects().getFirst())
                .isSameAs(input.batches().getFirst().getSubjects().getFirst());
    }

    @Test
    void roomFixedBatchPointsToTheMappedBatch() {
        BatchEntity batch = batch(20L, 60, List.of());
        RoomEntity homeRoom = room(30L, "CR-1", 70, batch);

        EngineInput input = mapper.toEngine(List.of(), List.of(batch), List.of(homeRoom));

        assertThat(input.rooms().getFirst().getFixedBatch()).isSameAs(input.batches().getFirst());
    }

    @Test
    void missingSubjectListsBecomeEmptyMutableLists() {
        TeacherEntity teacher = teacher(10L, "Asha", null);
        BatchEntity batch = batch(20L, 60, null);

        EngineInput input = mapper.toEngine(List.of(teacher), List.of(batch), List.of());

        List<Subject> batchSubjects = input.batches().getFirst().getSubjects();
        assertThat(batchSubjects).isEmpty();
        assertThat(input.teachers().getFirst().getSubjects()).isEmpty();
        // BaseAlgo sorts a batch's subjects in place.
        batchSubjects.add(Subject.builder().id(1L).build());
        assertThat(batchSubjects).hasSize(1);
    }

    @Test
    void batchSubjectsCanBeSortedEvenWhenTheEntityListIsImmutable() {
        SubjectEntity lab = subject(2L, "CS101L", 1, 2, SubjectType.LAB);
        SubjectEntity maths = subject(1L, "MA101", 3, 1, SubjectType.LECTURE);

        EngineInput input = mapper.toEngine(List.of(), List.of(batch(20L, 60, List.of(maths, lab))), List.of());

        List<Subject> subjects = input.batches().getFirst().getSubjects();
        subjects.sort((a, b) -> Integer.compare(b.getSlotDuration(), a.getSlotDuration()));
        assertThat(subjects).extracting(Subject::getId).containsExactly(2L, 1L);
    }
}
