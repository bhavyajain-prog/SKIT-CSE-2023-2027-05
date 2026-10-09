package com.kronos.web.dto;

import lombok.Data;

import java.util.List;
@Data
public class BatchDto {
    private Long id;
    private String name;
    private String section;
    private int strength;
    private List<Long>subjectIds;
}
