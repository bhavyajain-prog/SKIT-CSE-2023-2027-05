package com.kronos.service;

import com.kronos.persistence.entity.RoomEntity;
import com.kronos.persistence.repository.BatchRepository;
import com.kronos.persistence.repository.RoomRepository;
import com.kronos.persistence.repository.ScheduledSessionRepository;
import com.kronos.persistence.repository.SessionRepository;
import com.kronos.service.exception.ConflictException;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.service.mapper.RoomMapper;
import com.kronos.web.dto.RoomDto;
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
public class RoomService {

    private final RoomRepository roomRepository;
    private final BatchRepository batchRepository;
    private final SessionRepository sessionRepository;
    private final ScheduledSessionRepository scheduledSessionRepository;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true)
    public Page<RoomDto> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable)
                .map(roomMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomDto getRoomById(Long id) {
        RoomEntity entity = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        return roomMapper.toDto(entity);
    }

    @Transactional
    public RoomDto createRoom(RoomDto dto) {
        String trimmedName = dto.getName().trim();
        if (roomRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ConflictException("Room with name '" + trimmedName + "' already exists");
        }

        if (dto.getFixedBatchId() != null) {
            if (!batchRepository.existsById(dto.getFixedBatchId())) {
                throw new ResourceNotFoundException("Batch", dto.getFixedBatchId());
            }
            if (roomRepository.existsByFixedBatchId(dto.getFixedBatchId())) {
                throw new ConflictException("Batch with id " + dto.getFixedBatchId() + " is already assigned to another room");
            }
        }

        RoomEntity entity = roomMapper.toEntity(dto);
        entity.setName(trimmedName);
        RoomEntity saved = roomRepository.save(entity);
        log.info("Created room with id: {} and name: {}", saved.getId(), saved.getName());
        return roomMapper.toDto(saved);
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomDto dto) {
        RoomEntity existing = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));

        String trimmedName = dto.getName().trim();
        if (roomRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new ConflictException("Room with name '" + trimmedName + "' already exists");
        }

        if (dto.getFixedBatchId() != null) {
            if (!batchRepository.existsById(dto.getFixedBatchId())) {
                throw new ResourceNotFoundException("Batch", dto.getFixedBatchId());
            }
            if (roomRepository.existsByFixedBatchId(dto.getFixedBatchId())) {
                // Check if it's already assigned to a DIFFERENT room
                RoomEntity roomWithBatch = roomRepository.findAll().stream()
                        .filter(r -> r.getFixedBatch() != null && r.getFixedBatch().getId().equals(dto.getFixedBatchId()))
                        .findFirst()
                        .orElse(null);
                if (roomWithBatch != null && !roomWithBatch.getId().equals(id)) {
                    throw new ConflictException("Batch with id " + dto.getFixedBatchId() + " is already assigned to another room");
                }
            }
        }

        roomMapper.updateEntity(dto, existing);
        existing.setName(trimmedName);
        if (dto.getFixedBatchId() == null) {
            existing.setFixedBatch(null);
        }

        RoomEntity updated = roomRepository.save(existing);
        log.info("Updated room with id: {}", updated.getId());
        return roomMapper.toDto(updated);
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room", id);
        }

        if (sessionRepository.existsByPreferredRoomId(id)) {
            throw new ConflictException("Cannot delete room with id " + id + " because it is referenced as a preferred room in existing sessions");
        }

        if (scheduledSessionRepository.existsByRoomId(id)) {
            throw new ConflictException("Cannot delete room with id " + id + " because it is referenced in scheduled sessions");
        }

        roomRepository.deleteById(id);
        log.info("Deleted room with id: {}", id);
    }
}
