package com.kronos.service;

import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.repository.BatchRepository;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.service.mapper.BatchMapper;
import com.kronos.web.dto.BatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;

    public List<BatchDto> getAllBatches() {
        return batchRepository.findAll().stream()
                .map(batchMapper::toDto)
                .toList();
    }

    public BatchDto getBatchById(Long id) {
        BatchEntity entity = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch", id));
        return batchMapper.toDto(entity);
    }

    @Transactional
    public BatchDto createBatch(BatchDto dto) {
        BatchEntity entity = batchMapper.toEntity(dto);
        BatchEntity saved = batchRepository.save(entity);
        return batchMapper.toDto(saved);
    }

    @Transactional
    public BatchDto updateBatch(Long id, BatchDto dto) {
        BatchEntity existing = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch", id));

        batchMapper.updateEntity(dto, existing);

        return batchMapper.toDto(batchRepository.save(existing));
    }

    @Transactional
    public void deleteBatch(Long id) {
        if (!batchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Batch", id);
        }
        batchRepository.deleteById(id);
    }

}
