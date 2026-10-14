package com.kronos.web;

import com.kronos.web.dto.RoomDto;
import com.kronos.service.RoomService;
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

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Endpoints for managing rooms, classrooms, and lecture halls")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @Operation(summary = "List rooms with pagination", description = "Retrieves a paginated list of rooms with optional sorting.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of rooms")
    })
    public ResponseEntity<Page<RoomDto>> getAllRooms(
            @ParameterObject
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(roomService.getAllRooms(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Fetches the details of a single room by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room found and returned"),
            @ApiResponse(responseCode = "404", description = "Room not found with the specified ID",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class)))
    })
    public ResponseEntity<RoomDto> getRoomById(
            @Parameter(description = "ID of the room to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new room", description = "Creates a new room. Room name must be unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request payload",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Room with the given name already exists",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class)))
    })
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody RoomDto dto) {
        RoomDto created = roomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing room", description = "Updates details of an existing room by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request payload",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Room not found with the specified ID",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Room name conflicts with another room",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class)))
    })
    public ResponseEntity<RoomDto> updateRoom(
            @Parameter(description = "ID of the room to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RoomDto dto) {
        return ResponseEntity.ok(roomService.updateRoom(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room", description = "Deletes a room by its ID. Returns 409 if the room is referenced in sessions or scheduled sessions.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found with the specified ID",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Room is currently referenced and cannot be deleted",
                    content = @Content(schema = @Schema(implementation = com.kronos.web.error.ApiError.class)))
    })
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "ID of the room to delete", example = "1")
            @PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
