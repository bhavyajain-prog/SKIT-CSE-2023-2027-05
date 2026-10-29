package com.kronos.service;

import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.repository.BatchRepository;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.service.mapper.BatchMapper;
import com.kronos.web.dto.BatchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private BatchMapper batchMapper;

    @InjectMocks
    private BatchService batchService;

    @Test
    void getAllBatchesMapsEveryRow() {
        BatchEntity a = new BatchEntity();
        BatchEntity b = new BatchEntity();
        BatchDto dtoA = new BatchDto();
        BatchDto dtoB = new BatchDto();
        when(batchRepository.findAll()).thenReturn(List.of(a, b));
        when(batchMapper.toDto(a)).thenReturn(dtoA);
        when(batchMapper.toDto(b)).thenReturn(dtoB);

        assertThat(batchService.getAllBatches()).containsExactly(dtoA, dtoB);
    }

    @Test
    void getBatchByIdReturnsMappedBatch() {
        BatchEntity entity = new BatchEntity();
        BatchDto dto = new BatchDto();
        when(batchRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(batchMapper.toDto(entity)).thenReturn(dto);

        assertThat(batchService.getBatchById(1L)).isSameAs(dto);
    }

    @Test
    void getBatchByIdThrowsWhenMissing() {
        when(batchRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> batchService.getBatchById(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Batch not found with id: 42");
    }

    @Test
    void createBatchSavesMappedEntity() {
        BatchDto request = new BatchDto();
        BatchEntity entity = new BatchEntity();
        BatchEntity saved = new BatchEntity();
        BatchDto response = new BatchDto();
        when(batchMapper.toEntity(request)).thenReturn(entity);
        when(batchRepository.save(entity)).thenReturn(saved);
        when(batchMapper.toDto(saved)).thenReturn(response);

        assertThat(batchService.createBatch(request)).isSameAs(response);
    }

    @Test
    void updateBatchAppliesChangesToExistingEntity() {
        BatchDto request = new BatchDto();
        BatchEntity existing = new BatchEntity();
        BatchDto response = new BatchDto();
        when(batchRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(batchRepository.save(existing)).thenReturn(existing);
        when(batchMapper.toDto(existing)).thenReturn(response);

        assertThat(batchService.updateBatch(1L, request)).isSameAs(response);
        verify(batchMapper).updateEntity(request, existing);
    }

    @Test
    void updateBatchThrowsWhenMissing() {
        when(batchRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> batchService.updateBatch(42L, new BatchDto()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(batchRepository, never()).save(any());
    }

    @Test
    void deleteBatchDeletesExistingRow() {
        when(batchRepository.existsById(1L)).thenReturn(true);

        batchService.deleteBatch(1L);

        verify(batchRepository).deleteById(1L);
    }

    @Test
    void deleteBatchThrowsWhenMissing() {
        when(batchRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> batchService.deleteBatch(42L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(batchRepository, never()).deleteById(any());
    }
}
