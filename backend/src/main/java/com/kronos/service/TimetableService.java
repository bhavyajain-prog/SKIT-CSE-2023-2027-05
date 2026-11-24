package com.kronos.service;

import com.kronos.engine.SessionGenerator;
import com.kronos.engine.algorithm.Algo;
import com.kronos.engine.algorithm.ConstraintPropagationAlgo;
import com.kronos.engine.model.ScheduledResult;
import com.kronos.engine.model.ScheduledSession;
import com.kronos.engine.model.Session;
import com.kronos.engine.model.TimetablePreferences;
import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.RoomEntity;
import com.kronos.persistence.entity.ScheduledSessionEntity;
import com.kronos.persistence.entity.SessionEntity;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.TeacherEntity;
import com.kronos.persistence.entity.TimetableEntity;
import com.kronos.persistence.repository.BatchRepository;
import com.kronos.persistence.repository.RoomRepository;
import com.kronos.persistence.repository.ScheduledSessionRepository;
import com.kronos.persistence.repository.SessionRepository;
import com.kronos.persistence.repository.TeacherRepository;
import com.kronos.persistence.repository.TimetableRepository;
import com.kronos.service.exception.BadRequestException;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.service.mapper.EngineModelMapper;
import com.kronos.service.mapper.EngineModelMapper.EngineInput;
import com.kronos.web.dto.TimetableDtos.GenerateRequest;
import com.kronos.web.dto.TimetableDtos.SessionSlot;
import com.kronos.web.dto.TimetableDtos.TimetableDetail;
import com.kronos.web.dto.TimetableDtos.TimetableSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Runs the scheduling engine over the stored master data and keeps the result.
 * <p>
 * Generation is synchronous: the request returns once the timetable is saved.
 * The async job API will later wrap {@link #generate} in a background job.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableService {
    // The engine's lunch check is "a session may not span slot lunchStart";
    // -1 matches no slot, so it means no lunch break.
    private static final int NO_LUNCH = -1;

    private static final Comparator<ScheduledSessionEntity> PLACEMENT_ORDER =
            Comparator.comparingInt(ScheduledSessionEntity::getDayOfWeek)
                    .thenComparingInt(ScheduledSessionEntity::getStartSlot)
                    .thenComparing(ScheduledSessionEntity::getId);

    private final TeacherRepository teacherRepository;
    private final BatchRepository batchRepository;
    private final RoomRepository roomRepository;
    private final TimetableRepository timetableRepository;
    private final SessionRepository sessionRepository;
    private final ScheduledSessionRepository scheduledSessionRepository;
    private final EngineModelMapper engineModelMapper;

    @Transactional
    public TimetableDetail generate(GenerateRequest request) {
        int lunchStart = request.lunchStart() == null ? NO_LUNCH : request.lunchStart();
        if (lunchStart >= request.maxSlots()) {
            throw new BadRequestException("lunchStart must be less than maxSlots");
        }

        List<TeacherEntity> teachers = teacherRepository.findAll();
        List<BatchEntity> batches = batchRepository.findAll();
        List<RoomEntity> rooms = roomRepository.findAll();
        if (batches.isEmpty()) {
            throw new BadRequestException("There are no batches to schedule");
        }
        if (rooms.isEmpty()) {
            throw new BadRequestException("There are no rooms to schedule into");
        }

        EngineInput input = engineModelMapper.toEngine(teachers, batches, rooms);
        List<Session> sessions = generateSessions(input);
        Algo algo = new ConstraintPropagationAlgo(sessions, List.of(), input.rooms(),
                request.weekDays(), request.maxSlots(), lunchStart, List.of());

        long started = System.nanoTime();
        ScheduledResult result = algo.generateTimeTable();
        log.info("Generated timetable '{}': {} placed, {} unplaced in {} ms", request.name(),
                result.getSessions().size(), result.getUnscheduledSessions().size(),
                (System.nanoTime() - started) / 1_000_000);

        TimetableEntity timetable = timetableRepository.save(TimetableEntity.builder()
                .name(request.name().trim())
                .weekDays(request.weekDays())
                .maxSlots(request.maxSlots())
                .lunchStart(request.lunchStart())
                .build());
        return save(timetable, result, new EntityLookup(teachers, batches, rooms));
    }

    @Transactional(readOnly = true)
    public List<TimetableSummary> list() {
        return timetableRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(timetable -> {
                    long total = sessionRepository.countByTimetableId(timetable.getId());
                    long scheduled = scheduledSessionRepository.countByTimetableId(timetable.getId());
                    return toSummary(timetable, scheduled, total - scheduled);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public TimetableDetail get(Long id) {
        TimetableEntity timetable = findOrThrow(id);
        List<ScheduledSessionEntity> placements = scheduledSessionRepository.findDetailedByTimetableId(id);
        Set<Long> placedSessionIds = placements.stream()
                .map(placement -> placement.getSession().getId())
                .collect(Collectors.toSet());
        List<SessionEntity> unplaced = sessionRepository.findDetailedByTimetableId(id).stream()
                .filter(session -> !placedSessionIds.contains(session.getId()))
                .toList();
        return toDetail(timetable, placements, unplaced);
    }

    @Transactional
    public void delete(Long id) {
        TimetableEntity timetable = findOrThrow(id);
        // Placements reference sessions, so they go first. The bulk deletes
        // clear the persistence context, so no stale session rows get flushed.
        scheduledSessionRepository.deleteByTimetableId(id);
        sessionRepository.deleteByTimetableId(id);
        timetableRepository.delete(timetable);
    }

    private List<Session> generateSessions(EngineInput input) {
        try {
            return new SessionGenerator(input.teachers(), input.batches(), input.rooms(), new TimetablePreferences())
                    .generateAllSessions();
        } catch (RuntimeException e) {
            // The generator throws plain RuntimeExceptions for bad master data,
            // such as a batch subject that no teacher can take.
            throw new BadRequestException("Cannot build sessions: " + e.getMessage());
        }
    }

    private TimetableDetail save(TimetableEntity timetable, ScheduledResult result, EntityLookup lookup) {
        // Engine sessions have no equals/hashCode, so this map is keyed by
        // identity; it is linked so saved ids follow placement order.
        Map<Session, SessionEntity> sessionEntities = new LinkedHashMap<>();
        for (ScheduledSession placed : result.getSessions()) {
            sessionEntities.put(placed.getSession(), toSessionEntity(placed.getSession(), timetable, lookup));
        }
        for (Session session : result.getUnscheduledSessions()) {
            sessionEntities.put(session, toSessionEntity(session, timetable, lookup));
        }
        sessionRepository.saveAll(List.copyOf(sessionEntities.values()));

        List<ScheduledSessionEntity> placements = new ArrayList<>(result.getSessions().size());
        for (ScheduledSession placed : result.getSessions()) {
            placements.add(ScheduledSessionEntity.builder()
                    .timetable(timetable)
                    .session(sessionEntities.get(placed.getSession()))
                    .dayOfWeek(placed.getWeekDay().ordinal())
                    .startSlot(placed.getStartSlot())
                    .room(lookup.room(placed.getAssignedRoom().getId()))
                    .build());
        }
        scheduledSessionRepository.saveAll(placements);
        placements.sort(PLACEMENT_ORDER);

        List<SessionEntity> unplaced = result.getUnscheduledSessions().stream()
                .map(sessionEntities::get)
                .toList();
        return toDetail(timetable, placements, unplaced);
    }

    private SessionEntity toSessionEntity(Session session, TimetableEntity timetable, EntityLookup lookup) {
        return SessionEntity.builder()
                .timetable(timetable)
                .subject(lookup.subject(session.getSubject().getId()))
                .teacher(lookup.teacher(session.getTeacher().getId()))
                .batch(lookup.batch(session.getBatch().getId()))
                .preferredRoom(session.getPreferredRoom() == null ? null : lookup.room(session.getPreferredRoom().getId()))
                .slotDuration(session.getSlotDuration())
                .build();
    }

    private TimetableDetail toDetail(TimetableEntity timetable, List<ScheduledSessionEntity> placements,
                                     List<SessionEntity> unplaced) {
        return new TimetableDetail(
                toSummary(timetable, placements.size(), unplaced.size()),
                placements.stream().map(p -> toSlot(p.getSession(), p)).toList(),
                unplaced.stream().map(s -> toSlot(s, null)).toList());
    }

    private static TimetableSummary toSummary(TimetableEntity timetable, long scheduled, long unscheduled) {
        return new TimetableSummary(timetable.getId(), timetable.getName(), timetable.getWeekDays(),
                timetable.getMaxSlots(), timetable.getLunchStart(), timetable.getCreatedAt(), scheduled, unscheduled);
    }

    private static SessionSlot toSlot(SessionEntity session, ScheduledSessionEntity placement) {
        SubjectEntity subject = session.getSubject();
        TeacherEntity teacher = session.getTeacher();
        BatchEntity batch = session.getBatch();
        boolean placed = placement != null;
        return new SessionSlot(
                session.getId(),
                placed ? placement.getDayOfWeek() : null,
                placed ? DayOfWeek.of(placement.getDayOfWeek() + 1).name() : null,
                placed ? placement.getStartSlot() : null,
                session.getSlotDuration(),
                subject.getId(),
                subject.getSubjectCode(),
                subject.getSubjectName(),
                teacher.getId(),
                fullName(teacher),
                batch.getId(),
                batch.getName() + " " + batch.getSection(),
                placed ? placement.getRoom().getId() : null,
                placed ? placement.getRoom().getName() : null);
    }

    private static String fullName(TeacherEntity teacher) {
        String last = teacher.getLastName();
        return last == null || last.isBlank() ? teacher.getFirstName() : teacher.getFirstName() + " " + last;
    }

    private TimetableEntity findOrThrow(Long id) {
        return timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable", id));
    }

    /**
     * Finds the loaded entities behind the engine's ids, so saving a result
     * needs no extra queries.
     */
    private static final class EntityLookup {
        private final Map<Long, TeacherEntity> teachers;
        private final Map<Long, BatchEntity> batches;
        private final Map<Long, RoomEntity> rooms;
        private final Map<Long, SubjectEntity> subjects = new HashMap<>();

        EntityLookup(List<TeacherEntity> teachers, List<BatchEntity> batches, List<RoomEntity> rooms) {
            this.teachers = byId(teachers, TeacherEntity::getId);
            this.batches = byId(batches, BatchEntity::getId);
            this.rooms = byId(rooms, RoomEntity::getId);
            batches.forEach(batch -> addSubjects(batch.getSubjects()));
            teachers.forEach(teacher -> addSubjects(teacher.getSubjects()));
        }

        TeacherEntity teacher(long id) {
            return teachers.get(id);
        }

        BatchEntity batch(long id) {
            return batches.get(id);
        }

        RoomEntity room(long id) {
            return rooms.get(id);
        }

        SubjectEntity subject(long id) {
            return subjects.get(id);
        }

        private void addSubjects(List<SubjectEntity> list) {
            if (list != null) {
                list.forEach(subject -> subjects.putIfAbsent(subject.getId(), subject));
            }
        }

        private static <T> Map<Long, T> byId(List<T> entities, Function<T, Long> id) {
            return entities.stream().collect(Collectors.toMap(id, Function.identity()));
        }
    }
}
