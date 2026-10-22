package com.kronos.persistence.repository;

import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {

    boolean existsBySubjectCodeIgnoreCase(String subjectCode);

    boolean existsBySubjectCodeIgnoreCaseAndIdNot(String subjectCode, Long id);

    Optional<SubjectEntity> findBySubjectCodeIgnoreCase(String subjectCode);

    List<SubjectEntity> findBySubjectType(SubjectType subjectType);
}
