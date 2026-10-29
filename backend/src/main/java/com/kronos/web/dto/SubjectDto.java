package com.kronos.web.dto;

import com.kronos.enums.SubjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Subject (Course offering)")
public class SubjectDto {

    @Schema(description = "Unique identifier of the subject", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Subject code must not be blank")
    @Size(max = 50, message = "Subject code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Subject code must contain only alphanumeric characters, dashes, or underscores")
    @Schema(description = "Unique curriculum course code", example = "5CS4-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subjectCode;

    @NotBlank(message = "Subject name must not be blank")
    @Size(max = 255, message = "Subject name must not exceed 255 characters")
    @Schema(description = "Full descriptive name of the course", example = "Operating Systems", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subjectName;

    @NotNull(message = "Weight must not be null")
    @Min(value = 1, message = "Weight must be at least 1")
    @Max(value = 10, message = "Weight must not exceed 10")
    @Schema(description = "Weekly credit weight / periods required", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer weight;

    @Min(value = 1, message = "Slot duration must be at least 1")
    @Max(value = 4, message = "Slot duration must not exceed 4")
    @Schema(description = "Consecutive periods per session (1 for lecture, 2 for practical lab)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private int slotDuration;

    @NotNull(message = "Subject type must not be null")
    @Schema(description = "Instructional delivery mode (LECTURE or LAB)", example = "LECTURE", requiredMode = Schema.RequiredMode.REQUIRED)
    private SubjectType subjectType;
}
