package com.kronos.engine.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Teacher {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Subject> subjects;
}
