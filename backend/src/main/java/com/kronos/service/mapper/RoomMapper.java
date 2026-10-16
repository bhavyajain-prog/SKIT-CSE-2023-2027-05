package com.kronos.service.mapper;

import com.kronos.engine.model.Room;
import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.RoomEntity;
import com.kronos.web.dto.RoomDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = CentralMapperConfig.class, uses = {BatchMapper.class})
public interface RoomMapper {

    // 1. DB Entity <-> Engine POJO
    Room toEngine(RoomEntity entity);

    RoomEntity toEntity(Room pojo);

    // 2. DB Entity -> Web DTO
    @Mapping(target = "fixedBatchId", source = "fixedBatch.id")
    RoomDto toDto(RoomEntity entity);

    // 3. Web DTO -> DB Entity
    @Mapping(target = "fixedBatch", source = "fixedBatchId", qualifiedByName = "mapBatchIdToEntity")
    RoomEntity toEntity(RoomDto dto);

    // 3b. Web DTO -> existing DB Entity (PUT)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fixedBatch", source = "fixedBatchId", qualifiedByName = "mapBatchIdToEntity")
    void updateEntity(RoomDto dto, @MappingTarget RoomEntity entity);

    // 4. Convert batch ID to BatchEntity reference
    @Named("mapBatchIdToEntity")
    default BatchEntity mapBatchIdToEntity(Long batchId) {
        if (batchId == null) {
            return null;
        }
        BatchEntity batch = new BatchEntity();
        batch.setId(batchId);
        return batch;
    }
}
