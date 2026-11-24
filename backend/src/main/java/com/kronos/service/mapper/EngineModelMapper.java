package com.kronos.service.mapper;

import com.kronos.engine.model.Batch;
import com.kronos.engine.model.Room;
import com.kronos.engine.model.Subject;
import com.kronos.engine.model.Teacher;
import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.RoomEntity;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.TeacherEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts master-data entities into the engine's plain models.
 * <p>
 * Each call builds one consistent graph: a subject or batch referenced from
 * several places maps to the same engine object. The engine only needs the
 * ids to match, but sharing instances keeps memory flat on large inputs.
 * Lists are copied into mutable {@link ArrayList}s because some algorithms
 * sort them in place.
 */
@Component
public class EngineModelMapper {

    public EngineInput toEngine(List<TeacherEntity> teachers, List<BatchEntity> batches, List<RoomEntity> rooms) {
        Map<Long, Subject> subjects = new HashMap<>();
        Map<Long, Batch> batchesById = new HashMap<>();

        List<Teacher> engineTeachers = new ArrayList<>(teachers.size());
        for (TeacherEntity teacher : teachers) {
            engineTeachers.add(Teacher.builder()
                    .id(teacher.getId())
                    .firstName(teacher.getFirstName())
                    .lastName(teacher.getLastName())
                    .email(teacher.getEmail())
                    .subjects(toSubjects(teacher.getSubjects(), subjects))
                    .build());
        }

        List<Batch> engineBatches = new ArrayList<>(batches.size());
        for (BatchEntity batch : batches) {
            Batch engineBatch = Batch.builder()
                    .id(batch.getId())
                    .name(batch.getName())
                    .section(batch.getSection())
                    .strength(batch.getStrength())
                    .subjects(toSubjects(batch.getSubjects(), subjects))
                    .build();
            batchesById.put(batch.getId(), engineBatch);
            engineBatches.add(engineBatch);
        }

        List<Room> engineRooms = new ArrayList<>(rooms.size());
        for (RoomEntity room : rooms) {
            BatchEntity fixed = room.getFixedBatch();
            engineRooms.add(Room.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .capacity(room.getCapacity())
                    .fixedBatch(fixed == null ? null : batchesById.get(fixed.getId()))
                    .build());
        }

        return new EngineInput(engineTeachers, engineBatches, engineRooms);
    }

    private List<Subject> toSubjects(List<SubjectEntity> entities, Map<Long, Subject> cache) {
        List<Subject> result = new ArrayList<>();
        if (entities == null) {
            return result;
        }
        for (SubjectEntity entity : entities) {
            result.add(cache.computeIfAbsent(entity.getId(), id -> Subject.builder()
                    .id(id)
                    .name(entity.getSubjectName())
                    .weight(entity.getWeight())
                    .slotDuration(entity.getSlotDuration())
                    .subjectType(entity.getSubjectType())
                    .build()));
        }
        return result;
    }

    /**
     * The engine's view of the master data, ready for the session generator.
     */
    public record EngineInput(List<Teacher> teachers, List<Batch> batches, List<Room> rooms) {
    }
}
