package com.kronos.testsupport;

import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.RoomEntity;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.TeacherEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Unsaved master-data entities with ids already set, for unit tests that run
 * without a database.
 */
public final class MasterDataFixtures {

    private MasterDataFixtures() {
    }

    public static SubjectEntity subject(long id, String code, int weight, int slotDuration, SubjectType type) {
        SubjectEntity subject = SubjectEntity.builder()
                .subjectCode(code)
                .subjectName(code.startsWith("MA") ? "Maths" : "Programming Lab")
                .weight(weight)
                .slotDuration(slotDuration)
                .subjectType(type)
                .build();
        subject.setId(id);
        return subject;
    }

    public static TeacherEntity teacher(long id, String firstName, List<SubjectEntity> subjects) {
        TeacherEntity teacher = TeacherEntity.builder()
                .firstName(firstName)
                .lastName("Rao")
                .email(firstName.toLowerCase() + "@kronos.test")
                .subjects(subjects == null ? null : new ArrayList<>(subjects))
                .build();
        teacher.setId(id);
        return teacher;
    }

    public static BatchEntity batch(long id, int strength, List<SubjectEntity> subjects) {
        BatchEntity batch = BatchEntity.builder()
                .name("CSE")
                .section("A")
                .strength(strength)
                .subjects(subjects)
                .build();
        batch.setId(id);
        return batch;
    }

    public static RoomEntity room(long id, String name, int capacity, BatchEntity fixedBatch) {
        RoomEntity room = RoomEntity.builder()
                .name(name)
                .capacity(capacity)
                .fixedBatch(fixedBatch)
                .build();
        room.setId(id);
        return room;
    }
}
