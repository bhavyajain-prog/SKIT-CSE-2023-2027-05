package com.kronos.persistence.entity;

import com.kronos.enums.SubjectType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@Entity
@Table(name = "subjects")
public class SubjectEntity extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String subjectCode;
    @Column(nullable = false)
    private String subjectName;
    @Column(nullable = false)
    private Integer weight;
    @Column(nullable = false)
    private int slotDuration;
    @Column(nullable = false)
    private SubjectType subjectType;
}
