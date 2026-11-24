package com.kronos.web;

import com.kronos.service.TimetableService;
import com.kronos.web.dto.TimetableDtos.GenerateRequest;
import com.kronos.web.dto.TimetableDtos.TimetableDetail;
import com.kronos.web.dto.TimetableDtos.TimetableSummary;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;

    /**
     * Generates a timetable from all stored teachers, batches and rooms and
     * returns it once saved. Sessions the engine could not place are listed
     * under {@code unscheduled}.
     */
    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('TIMETABLE_GENERATE')")
    public TimetableDetail generate(@Valid @RequestBody GenerateRequest request) {
        return timetableService.generate(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TIMETABLE_READ')")
    public List<TimetableSummary> list() {
        return timetableService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TIMETABLE_READ')")
    public TimetableDetail get(@PathVariable Long id) {
        return timetableService.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('TIMETABLE_GENERATE')")
    public void delete(@PathVariable Long id) {
        timetableService.delete(id);
    }
}
