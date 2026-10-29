package com.kronos.web;

import com.kronos.service.SubjectService;
import com.kronos.web.dto.SubjectDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Tag(name = "Subjects", description = "Endpoints for managing academic subjects and curriculum offerings")
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    @Operation(summary = "List subjects with pagination", description = "Retrieves a paginated list of subjects with optional sorting.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved page of subjects")
    })
    public ResponseEntity<Page<SubjectDto>> getAllSubjects(
            @ParameterObject
            @PageableDefault(size = 20, sort = "subjectCode", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(subjectService.getAllSubjects(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "List all subjects", description = "Retrieves all subjects without pagination for drop-down selections.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of subjects")
    })
    public ResponseEntity<List<SubjectDto>> getAllSubjectsUnpaged() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subject by ID", description = "Fetches the details of a single subject by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subject found and returned"),
            @ApiResponse(responseCode = "404", description = "Subject not found with the specified ID",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class)))
    })
    public ResponseEntity<SubjectDto> getSubjectById(
            @Parameter(description = "ID of the subject to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @GetMapping("/by-code/{code}")
    @Operation(summary = "Get subject by course code", description = "Fetches the details of a subject by its unique curriculum code.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subject found and returned"),
            @ApiResponse(responseCode = "404", description = "Subject not found with the specified code",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class)))
    })
    public ResponseEntity<SubjectDto> getSubjectByCode(
            @Parameter(description = "Unique code of the subject", example = "5CS4-01")
            @PathVariable String code) {
        return ResponseEntity.ok(subjectService.getSubjectByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new subject", description = "Creates a new subject. Subject code must be unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subject created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request payload",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Subject with the specified code already exists",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class)))
    })
    public ResponseEntity<SubjectDto> createSubject(@Valid @RequestBody SubjectDto dto) {
        SubjectDto created = subjectService.createSubject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing subject", description = "Updates the fields of an existing subject by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subject updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request payload",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Subject not found with the specified ID",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Subject code is already in use by another subject",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class)))
    })
    public ResponseEntity<SubjectDto> updateSubject(
            @Parameter(description = "ID of the subject to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SubjectDto dto) {
        return ResponseEntity.ok(subjectService.updateSubject(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a subject by ID", description = "Deletes a subject. Fails with 409 Conflict if referenced by active sessions.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subject deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subject not found with the specified ID",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Subject cannot be deleted due to existing session references",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class)))
    })
    public ResponseEntity<Void> deleteSubject(
            @Parameter(description = "ID of the subject to delete", example = "1")
            @PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
