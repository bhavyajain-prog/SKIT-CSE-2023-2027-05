package com.kronos.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kronos.enums.SubjectType;
import com.kronos.service.SubjectService;
import com.kronos.service.exception.ConflictException;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.web.dto.SubjectDto;
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
class SubjectControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SubjectService subjectService;

    @InjectMocks
    private SubjectController subjectController;

    private SubjectDto sampleSubject;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subjectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        sampleSubject = SubjectDto.builder()
                .id(1L)
                .subjectCode("5CS4-01")
                .subjectName("Operating Systems")
                .weight(3)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/subjects returns 200 with paged content")
    void getAllSubjects_ReturnsOkWithPage() throws Exception {
        when(subjectService.getAllSubjects(any())).thenReturn(new PageImpl<>(List.of(sampleSubject), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].subjectCode").value("5CS4-01"))
                .andExpect(jsonPath("$.content[0].subjectName").value("Operating Systems"))
                .andExpect(jsonPath("$.content[0].weight").value(3))
                .andExpect(jsonPath("$.content[0].subjectType").value("LECTURE"));
    }

    @Test
    @DisplayName("GET /api/v1/subjects/all returns 200 with list of subjects")
    void getAllSubjectsUnpaged_ReturnsOk() throws Exception {
        when(subjectService.getAllSubjects()).thenReturn(List.of(sampleSubject));

        mockMvc.perform(get("/api/v1/subjects/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subjectCode").value("5CS4-01"))
                .andExpect(jsonPath("$[0].subjectName").value("Operating Systems"));
    }

    @Test
    @DisplayName("GET /api/v1/subjects/{id} returns 200 when subject exists")
    void getSubjectById_WhenExists_ReturnsOk() throws Exception {
        when(subjectService.getSubjectById(1L)).thenReturn(sampleSubject);

        mockMvc.perform(get("/api/v1/subjects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.subjectCode").value("5CS4-01"));
    }

    @Test
    @DisplayName("GET /api/v1/subjects/{id} returns 404 when subject not found")
    void getSubjectById_WhenNotFound_Returns404() throws Exception {
        when(subjectService.getSubjectById(99L)).thenThrow(new ResourceNotFoundException("Subject", 99L));

        mockMvc.perform(get("/api/v1/subjects/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/v1/subjects/by-code/{code} returns 200 when subject exists")
    void getSubjectByCode_WhenExists_ReturnsOk() throws Exception {
        when(subjectService.getSubjectByCode("5CS4-01")).thenReturn(sampleSubject);

        mockMvc.perform(get("/api/v1/subjects/by-code/5CS4-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectCode").value("5CS4-01"));
    }

    @Test
    @DisplayName("POST /api/v1/subjects returns 201 when payload is valid")
    void createSubject_WhenValid_Returns201Created() throws Exception {
        SubjectDto request = SubjectDto.builder()
                .subjectCode("5CS4-02")
                .subjectName("Compiler Design")
                .weight(4)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();

        SubjectDto created = SubjectDto.builder()
                .id(2L)
                .subjectCode("5CS4-02")
                .subjectName("Compiler Design")
                .weight(4)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();

        when(subjectService.createSubject(any(SubjectDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.subjectCode").value("5CS4-02"));
    }

    @Test
    @DisplayName("POST /api/v1/subjects returns 400 when subjectCode is blank")
    void createSubject_WhenBlankCode_Returns400BadRequest() throws Exception {
        SubjectDto invalid = SubjectDto.builder()
                .subjectCode("")
                .subjectName("Test")
                .weight(3)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();

        mockMvc.perform(post("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(subjectService, never()).createSubject(any());
    }

    @Test
    @DisplayName("POST /api/v1/subjects returns 409 when code already exists")
    void createSubject_WhenConflict_Returns409Conflict() throws Exception {
        when(subjectService.createSubject(any())).thenThrow(new ConflictException("Subject with code '5CS4-01' already exists"));

        mockMvc.perform(post("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleSubject)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("PUT /api/v1/subjects/{id} returns 200 when update is successful")
    void updateSubject_WhenValid_ReturnsOk() throws Exception {
        SubjectDto updated = SubjectDto.builder()
                .id(1L)
                .subjectCode("5CS4-01")
                .subjectName("Operating Systems (Updated)")
                .weight(4)
                .slotDuration(1)
                .subjectType(SubjectType.LECTURE)
                .build();

        when(subjectService.updateSubject(eq(1L), any(SubjectDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/subjects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectName").value("Operating Systems (Updated)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/subjects/{id} returns 204 when successful")
    void deleteSubject_WhenSuccess_Returns204NoContent() throws Exception {
        doNothing().when(subjectService).deleteSubject(1L);

        mockMvc.perform(delete("/api/v1/subjects/1"))
                .andExpect(status().isNoContent());

        verify(subjectService).deleteSubject(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/subjects/{id} returns 409 when referenced by active sessions")
    void deleteSubject_WhenConflict_Returns409Conflict() throws Exception {
        doThrow(new ConflictException("Cannot delete subject: Currently referenced by active scheduled sessions"))
                .when(subjectService).deleteSubject(1L);

        mockMvc.perform(delete("/api/v1/subjects/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
