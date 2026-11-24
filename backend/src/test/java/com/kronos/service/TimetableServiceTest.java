package com.kronos.service;

import com.kronos.enums.SubjectType;
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
import com.kronos.web.dto.TimetableDtos.GenerateRequest;
import com.kronos.web.dto.TimetableDtos.SessionSlot;
import com.kronos.web.dto.TimetableDtos.TimetableDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static com.kronos.testsupport.MasterDataFixtures.batch;
import static com.kronos.testsupport.MasterDataFixtures.room;
import static com.kronos.testsupport.MasterDataFixtures.subject;
import static com.kronos.testsupport.MasterDataFixtures.teacher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {

    private static final int MAX_SLOTS = 6;
    private static final int LUNCH = 3;

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private BatchRepository batchRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private TimetableRepository timetableRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private ScheduledSessionRepository scheduledSessionRepository;

    private TimetableService service;
    private final AtomicLong ids = new AtomicLong(100);

    @BeforeEach
    void setUp() {
        service = new TimetableService(teacherRepository, batchRepository, roomRepository, timetableRepository,
                sessionRepository, scheduledSessionRepository, new EngineModelMapper());
        // Stand in for the database assigning identity ids on save.
        lenient().when(timetableRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        lenient().when(sessionRepository.saveAll(anyIterable())).thenAnswer(inv -> {
            List<SessionEntity> saved = inv.getArgument(0);
            saved.forEach(this::withId);
            return saved;
        });
        lenient().when(scheduledSessionRepository.saveAll(anyIterable())).thenAnswer(inv -> {
            List<ScheduledSessionEntity> saved = inv.getArgument(0);
            saved.forEach(this::withId);
            return saved;
        });
    }

    @Test
    void generatePlacesEverySessionAndKeepsClearOfLunch() {
        SubjectEntity maths = subject(1L, "MA101", 3, 1, SubjectType.LECTURE);
        givenMasterData(maths, 30, 40);

        TimetableDetail detail = service.generate(request(LUNCH));

        assertThat(detail.summary().name()).isEqualTo("Odd semester");
        assertThat(detail.summary().scheduledCount()).isEqualTo(3);
        assertThat(detail.summary().unscheduledCount()).isZero();
        assertThat(detail.unscheduled()).isEmpty();
        assertThat(detail.scheduled()).hasSize(3).allSatisfy(slot -> {
            assertThat(slot.startSlot()).isNotEqualTo(LUNCH).isBetween(0, MAX_SLOTS - 1);
            assertThat(slot.dayOfWeek()).isBetween(0, 4);
            assertThat(slot.subjectCode()).isEqualTo("MA101");
            assertThat(slot.teacherName()).isEqualTo("Asha Rao");
            assertThat(slot.batchName()).isEqualTo("CSE A");
            assertThat(slot.roomName()).isEqualTo("LT-1");
        });
        verify(sessionRepository).saveAll(anyIterable());
        verify(scheduledSessionRepository).saveAll(anyIterable());
    }

    @Test
    void scheduledSlotsComeBackInDayThenSlotOrder() {
        givenMasterData(subject(1L, "MA101", 5, 1, SubjectType.LECTURE), 30, 40);

        List<SessionSlot> slots = service.generate(request(null)).scheduled();

        for (int i = 1; i < slots.size(); i++) {
            SessionSlot previous = slots.get(i - 1);
            SessionSlot current = slots.get(i);
            int previousKey = previous.dayOfWeek() * MAX_SLOTS + previous.startSlot();
            int currentKey = current.dayOfWeek() * MAX_SLOTS + current.startSlot();
            assertThat(currentKey).isGreaterThanOrEqualTo(previousKey);
        }
    }

    @Test
    void dayNameMatchesTheDayIndex() {
        givenMasterData(subject(1L, "MA101", 5, 1, SubjectType.LECTURE), 30, 40);

        service.generate(request(null)).scheduled().forEach(slot -> assertThat(slot.day())
                .isEqualTo(java.time.DayOfWeek.of(slot.dayOfWeek() + 1).name()));
    }

    @Test
    void batchTooBigForEveryRoomLeavesSessionsUnscheduled() {
        givenMasterData(subject(1L, "MA101", 2, 1, SubjectType.LECTURE), 120, 40);

        TimetableDetail detail = service.generate(request(LUNCH));

        assertThat(detail.scheduled()).isEmpty();
        assertThat(detail.unscheduled()).hasSize(2).allSatisfy(slot -> {
            assertThat(slot.dayOfWeek()).isNull();
            assertThat(slot.startSlot()).isNull();
            assertThat(slot.roomId()).isNull();
            assertThat(slot.sessionId()).isNotNull();
        });
        assertThat(detail.summary().unscheduledCount()).isEqualTo(2);
    }

    @Test
    void lunchStartAtOrAfterMaxSlotsIsRejected() {
        assertThatThrownBy(() -> service.generate(request(MAX_SLOTS)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("lunchStart must be less than maxSlots");
        verify(timetableRepository, never()).save(any());
    }

    @Test
    void noBatchesIsRejected() {
        when(teacherRepository.findAll()).thenReturn(List.of());
        when(batchRepository.findAll()).thenReturn(List.of());
        when(roomRepository.findAll()).thenReturn(List.of(room(30L, "LT-1", 40, null)));

        assertThatThrownBy(() -> service.generate(request(LUNCH)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("There are no batches to schedule");
    }

    @Test
    void noRoomsIsRejected() {
        when(teacherRepository.findAll()).thenReturn(List.of());
        when(batchRepository.findAll()).thenReturn(List.of(batch(20L, 30, List.of())));
        when(roomRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> service.generate(request(LUNCH)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("There are no rooms to schedule into");
    }

    @Test
    void subjectNobodyTeachesIsRejectedBeforeAnythingIsSaved() {
        SubjectEntity maths = subject(1L, "MA101", 3, 1, SubjectType.LECTURE);
        when(teacherRepository.findAll()).thenReturn(List.of(teacher(10L, "Asha", List.of())));
        when(batchRepository.findAll()).thenReturn(List.of(batch(20L, 30, List.of(maths))));
        when(roomRepository.findAll()).thenReturn(List.of(room(30L, "LT-1", 40, null)));

        assertThatThrownBy(() -> service.generate(request(LUNCH)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Maths");
        verify(timetableRepository, never()).save(any());
    }

    @Test
    void getUnknownTimetableIs404() {
        when(timetableRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(5L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getSplitsSessionsIntoScheduledAndUnscheduled() {
        TimetableEntity timetable = timetable(7L);
        SubjectEntity maths = subject(1L, "MA101", 2, 1, SubjectType.LECTURE);
        TeacherEntity asha = teacher(10L, "Asha", List.of(maths));
        BatchEntity cse = batch(20L, 30, List.of(maths));
        RoomEntity lt1 = room(30L, "LT-1", 40, null);
        SessionEntity placed = session(41L, maths, asha, cse);
        SessionEntity left = session(42L, maths, asha, cse);
        ScheduledSessionEntity placement = ScheduledSessionEntity.builder()
                .session(placed).dayOfWeek(2).startSlot(1).room(lt1).timetable(timetable).build();

        when(timetableRepository.findById(7L)).thenReturn(Optional.of(timetable));
        when(scheduledSessionRepository.findDetailedByTimetableId(7L)).thenReturn(List.of(placement));
        when(sessionRepository.findDetailedByTimetableId(7L)).thenReturn(List.of(placed, left));

        TimetableDetail detail = service.get(7L);

        assertThat(detail.scheduled()).singleElement().satisfies(slot -> {
            assertThat(slot.sessionId()).isEqualTo(41L);
            assertThat(slot.day()).isEqualTo("WEDNESDAY");
            assertThat(slot.roomName()).isEqualTo("LT-1");
        });
        assertThat(detail.unscheduled()).singleElement()
                .satisfies(slot -> assertThat(slot.sessionId()).isEqualTo(42L));
    }

    @Test
    void listReportsCountsPerTimetable() {
        TimetableEntity timetable = timetable(7L);
        when(timetableRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(timetable));
        when(sessionRepository.countByTimetableId(7L)).thenReturn(10L);
        when(scheduledSessionRepository.countByTimetableId(7L)).thenReturn(8L);

        assertThat(service.list()).singleElement().satisfies(summary -> {
            assertThat(summary.scheduledCount()).isEqualTo(8);
            assertThat(summary.unscheduledCount()).isEqualTo(2);
        });
    }

    @Test
    void deleteRemovesPlacementsBeforeSessions() {
        TimetableEntity timetable = timetable(7L);
        when(timetableRepository.findById(7L)).thenReturn(Optional.of(timetable));

        service.delete(7L);

        InOrder order = inOrder(scheduledSessionRepository, sessionRepository, timetableRepository);
        order.verify(scheduledSessionRepository).deleteByTimetableId(7L);
        order.verify(sessionRepository).deleteByTimetableId(7L);
        order.verify(timetableRepository).delete(timetable);
    }

    @Test
    void deleteUnknownTimetableIs404() {
        when(timetableRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(5L)).isInstanceOf(ResourceNotFoundException.class);
        verify(sessionRepository, never()).deleteByTimetableId(any());
    }

    private void givenMasterData(SubjectEntity subject, int batchStrength, int roomCapacity) {
        when(teacherRepository.findAll()).thenReturn(List.of(teacher(10L, "Asha", List.of(subject))));
        when(batchRepository.findAll()).thenReturn(List.of(batch(20L, batchStrength, List.of(subject))));
        when(roomRepository.findAll()).thenReturn(List.of(room(30L, "LT-1", roomCapacity, null)));
    }

    private static GenerateRequest request(Integer lunchStart) {
        return new GenerateRequest("Odd semester", 5, MAX_SLOTS, lunchStart);
    }

    private static TimetableEntity timetable(long id) {
        TimetableEntity timetable = TimetableEntity.builder()
                .name("Odd semester").weekDays(5).maxSlots(MAX_SLOTS).lunchStart(LUNCH).build();
        timetable.setId(id);
        return timetable;
    }

    private static SessionEntity session(long id, SubjectEntity subject, TeacherEntity teacher, BatchEntity batch) {
        SessionEntity session = SessionEntity.builder()
                .subject(subject).teacher(teacher).batch(batch).slotDuration(1).build();
        session.setId(id);
        return session;
    }

    private <T extends com.kronos.persistence.entity.BaseEntity> T withId(T entity) {
        entity.setId(ids.incrementAndGet());
        return entity;
    }
}
