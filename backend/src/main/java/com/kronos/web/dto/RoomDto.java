package com.kronos.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Room (classroom/laboratory)")
public class RoomDto {

    @Schema(description = "Unique identifier of the room", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Room name must not be blank")
    @Size(max = 255, message = "Room name must not exceed 255 characters")
    @Schema(description = "Unique name or number of the room", example = "LT-101", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Schema(description = "Seating capacity of the room", example = "60", requiredMode = Schema.RequiredMode.REQUIRED)
    private int capacity;

    @Schema(description = "ID of the batch permanently assigned to this room, if any", example = "2")
    private Long fixedBatchId;
}
