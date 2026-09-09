package com.kronos.engine.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Batch {
    private long id;
    private String name;
    private String section;
    private int strength;
    private List<Subject> subjects;
}
