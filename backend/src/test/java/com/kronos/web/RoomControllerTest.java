package com.kronos.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kronos.service.RoomService;
import com.kronos.service.exception.ConflictException;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.web.dto.RoomDto;
import com.kronos.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private RoomDto sampleRoom;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        sampleRoom = RoomDto.builder()
                .id(1L)
                .name("LT-101")
                .capacity(80)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/rooms returns 200 with paginated rooms")
    void getAllRooms_returns200() throws Exception {
        when(roomService.getAllRooms(any()))
                .thenReturn(new PageImpl<>(List.of(sampleRoom), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("LT-101"))
                .andExpect(jsonPath("$.content[0].capacity").value(80));
    }

    @Test
    @DisplayName("GET /api/v1/rooms/{id} returns 200 when found")
    void getRoomById_whenFound_returns200() throws Exception {
        when(roomService.getRoomById(1L)).thenReturn(sampleRoom);

        mockMvc.perform(get("/api/v1/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("LT-101"))
                .andExpect(jsonPath("$.capacity").value(80));
    }

    @Test
    @DisplayName("GET /api/v1/rooms/{id} returns 404 when not found")
    void getRoomById_whenNotFound_returns404() throws Exception {
        when(roomService.getRoomById(99L)).thenThrow(new ResourceNotFoundException("Room", 99L));

        mockMvc.perform(get("/api/v1/rooms/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Room not found with id: 99"));
    }

    @Test
    @DisplayName("POST /api/v1/rooms returns 201 when valid")
    void createRoom_whenValid_returns201() throws Exception {
        when(roomService.createRoom(any(RoomDto.class))).thenReturn(sampleRoom);

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRoom)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("LT-101"));
    }

    @Test
    @DisplayName("POST /api/v1/rooms returns 400 when validation fails")
    void createRoom_whenInvalid_returns400() throws Exception {
        RoomDto invalidDto = RoomDto.builder().name("").capacity(0).build();

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/rooms returns 409 on duplicate room name")
    void createRoom_whenDuplicate_returns409() throws Exception {
        when(roomService.createRoom(any(RoomDto.class)))
                .thenThrow(new ConflictException("Room with name 'LT-101' already exists"));

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRoom)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Room with name 'LT-101' already exists"));
    }

    @Test
    @DisplayName("PUT /api/v1/rooms/{id} returns 200 when updated")
    void updateRoom_whenValid_returns200() throws Exception {
        RoomDto updated = RoomDto.builder().id(1L).name("LT-102").capacity(90).build();
        when(roomService.updateRoom(eq(1L), any(RoomDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("LT-102"))
                .andExpect(jsonPath("$.capacity").value(90));
    }

    @Test
    @DisplayName("DELETE /api/v1/rooms/{id} returns 204 when deleted")
    void deleteRoom_whenValid_returns204() throws Exception {
        doNothing().when(roomService).deleteRoom(1L);

        mockMvc.perform(delete("/api/v1/rooms/1"))
                .andExpect(status().isNoContent());

        verify(roomService).deleteRoom(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/rooms/{id} returns 409 when room is referenced")
    void deleteRoom_whenReferenced_returns409() throws Exception {
        doThrow(new ConflictException("Cannot delete room with id 1 because it is referenced in scheduled sessions"))
                .when(roomService).deleteRoom(1L);

        mockMvc.perform(delete("/api/v1/rooms/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Cannot delete room with id 1 because it is referenced in scheduled sessions"));
    }
}
