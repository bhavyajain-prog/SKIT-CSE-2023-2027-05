-- subject_type was created as INTEGER (enum ordinal), but SubjectEntity maps it
-- with @Enumerated(EnumType.STRING), so Hibernate schema validation expects VARCHAR.
-- Convert the column, translating existing ordinals to their enum names.
ALTER TABLE subjects
    ALTER COLUMN subject_type TYPE VARCHAR(255)
    USING CASE subject_type
        WHEN 0 THEN 'LECTURE'
        WHEN 1 THEN 'LAB'
    END;
