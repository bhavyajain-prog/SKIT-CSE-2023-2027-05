package com.kronos.web;

import com.kronos.service.FacultyService;
import com.kronos.web.dto.FacultyDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {
    private final FacultyService facultyService;

    @GetMapping
    @PreAuthorize("hasAuthority('FACULTY_READ')")
    public List<FacultyDto> getAll() {
        return facultyService.getAllFaculty();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FACULTY_READ')")
    public FacultyDto getById(@PathVariable Long id) {
        return facultyService.getFacultyById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('FACULTY_WRITE')")
    public FacultyDto create(@Valid @RequestBody FacultyDto dto) {
        return facultyService.createFaculty(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FACULTY_WRITE')")
    public FacultyDto update(@PathVariable Long id, @Valid @RequestBody FacultyDto dto) {
        return facultyService.updateFaculty(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('FACULTY_WRITE')")
    public void delete(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }
}
