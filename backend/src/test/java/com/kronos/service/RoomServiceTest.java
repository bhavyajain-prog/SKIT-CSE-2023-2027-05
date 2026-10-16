package com.kronos.service;

import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.RoomEntity;
import com.kronos.persistence.repository.BatchRepository;
import com.kronos.persistence.repository.RoomRepository;
import com.kronos.persistence.repository.ScheduledSessionRepository;
import com.kronos.persistence.repository.SessionRepository;
import com.kronos.service.exception.ConflictException;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.service.mapper.RoomMapper;
import com.kronos.web.dto.RoomDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ScheduledSessionRepository scheduledSessionRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    private RoomEntity roomEntity;
    private RoomDto roomDto;

    @BeforeEach
    void setUp() {
        roomEntity = RoomEntity.builder()
                .name("Room 101")
                .capacity(60)
                .build();
        roomEntity.setId(1L);

        roomDto = RoomDto.builder()
                .id(1L)
                .name("Room 101")
                .capacity(60)
                .build();
    }

    @Test
    @DisplayName("getAllRooms returns paged room DTOs")
    void getAllRooms_paged_returnsPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoomEntity> entityPage = new PageImpl<>(List.of(roomEntity));

        when(roomRepository.findAll(pageable)).thenReturn(entityPage);
        when(roomMapper.toDto(roomEntity)).thenReturn(roomDto);

        Page<RoomDto> result = roomService.getAllRooms(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Room 101");
        verify(roomRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getRoomById returns DTO when room exists")
    void getRoomById_whenFound_returnsDto() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(roomEntity));
        when(roomMapper.toDto(roomEntity)).thenReturn(roomDto);

        RoomDto result = roomService.getRoomById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Room 101");
    }

    @Test
    @DisplayName("getRoomById throws ResourceNotFoundException when room not found")
    void getRoomById_whenNotFound_throwsException() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room not found with id: 99");
    }

    @Test
    @DisplayName("createRoom successfully creates room")
    void createRoom_whenValid_savesAndReturnsDto() {
        when(roomRepository.existsByNameIgnoreCase("Room 101")).thenReturn(false);
        when(roomMapper.toEntity(roomDto)).thenReturn(roomEntity);
        when(roomRepository.save(roomEntity)).thenReturn(roomEntity);
        when(roomMapper.toDto(roomEntity)).thenReturn(roomDto);

        RoomDto result = roomService.createRoom(roomDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Room 101");
        verify(roomRepository).save(roomEntity);
    }

    @Test
    @DisplayName("createRoom throws ConflictException on duplicate room name")
    void createRoom_whenDuplicateName_throwsConflictException() {
        when(roomRepository.existsByNameIgnoreCase("Room 101")).thenReturn(true);

        assertThatThrownBy(() -> roomService.createRoom(roomDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Room with name 'Room 101' already exists");

        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("createRoom throws ResourceNotFoundException when fixedBatchId does not exist")
    void createRoom_whenInvalidFixedBatch_throwsException() {
        roomDto.setFixedBatchId(99L);
        when(roomRepository.existsByNameIgnoreCase("Room 101")).thenReturn(false);
        when(batchRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> roomService.createRoom(roomDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Batch not found with id: 99");

        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateRoom successfully updates existing room")
    void updateRoom_whenValid_updatesAndReturnsDto() {
        RoomDto updateDto = RoomDto.builder().name("Room 101-A").capacity(70).build();

        when(roomRepository.findById(1L)).thenReturn(Optional.of(roomEntity));
        when(roomRepository.existsByNameIgnoreCaseAndIdNot("Room 101-A", 1L)).thenReturn(false);
        when(roomRepository.save(roomEntity)).thenReturn(roomEntity);
        when(roomMapper.toDto(roomEntity)).thenReturn(updateDto);

        RoomDto result = roomService.updateRoom(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Room 101-A");
        verify(roomMapper).updateEntity(updateDto, roomEntity);
        verify(roomRepository).save(roomEntity);
    }

    @Test
    @DisplayName("updateRoom throws ConflictException when new name is taken by another room")
    void updateRoom_whenDuplicateName_throwsConflictException() {
        RoomDto updateDto = RoomDto.builder().name("Room 102").capacity(70).build();

        when(roomRepository.findById(1L)).thenReturn(Optional.of(roomEntity));
        when(roomRepository.existsByNameIgnoreCaseAndIdNot("Room 102", 1L)).thenReturn(true);

        assertThatThrownBy(() -> roomService.updateRoom(1L, updateDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Room with name 'Room 102' already exists");

        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteRoom successfully deletes room")
    void deleteRoom_whenValid_deletesRoom() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        when(sessionRepository.existsByPreferredRoomId(1L)).thenReturn(false);
        when(scheduledSessionRepository.existsByRoomId(1L)).thenReturn(false);

        roomService.deleteRoom(1L);

        verify(roomRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteRoom throws ResourceNotFoundException if room does not exist")
    void deleteRoom_whenNotFound_throwsException() {
        when(roomRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> roomService.deleteRoom(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room not found with id: 99");

        verify(roomRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteRoom throws ConflictException if room is referenced by sessions")
    void deleteRoom_whenReferencedBySession_throwsConflictException() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        when(sessionRepository.existsByPreferredRoomId(1L)).thenReturn(true);

        assertThatThrownBy(() -> roomService.deleteRoom(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("referenced as a preferred room in existing sessions");

        verify(roomRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteRoom throws ConflictException if room is referenced by scheduled sessions")
    void deleteRoom_whenReferencedByScheduledSession_throwsConflictException() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        when(sessionRepository.existsByPreferredRoomId(1L)).thenReturn(false);
        when(scheduledSessionRepository.existsByRoomId(1L)).thenReturn(true);

        assertThatThrownBy(() -> roomService.deleteRoom(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("referenced in scheduled sessions");

        verify(roomRepository, never()).deleteById(any());
    }
}
