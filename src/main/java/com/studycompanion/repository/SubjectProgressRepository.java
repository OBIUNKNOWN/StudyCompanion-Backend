package com.studycompanion.repository;

import com.studycompanion.entity.SubjectProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectProgressRepository extends JpaRepository<SubjectProgress, Long> {
    List<SubjectProgress> findAllBySubject_Owner_Id(Long ownerId);

    Optional<SubjectProgress> findBySubject_IdAndSubject_Owner_Id(Long subjectId, Long ownerId);

    boolean existsBySubject_IdAndSubject_Owner_Id(Long subjectId, Long ownerId);
}
