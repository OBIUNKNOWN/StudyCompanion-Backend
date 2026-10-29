package com.studycompanion.repository;

import com.studycompanion.entity.AppUser;
import com.studycompanion.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByOwner_Id(Long ownerId);

    Optional<Subject> findByIdAndOwner_Id(Long id, Long ownerId);

    boolean existsByNameIgnoreCaseAndOwner_Id(String name, Long ownerId);

    boolean existsByNameIgnoreCaseAndOwner_IdAndIdNot(String name, Long ownerId, Long id);

    List<Subject> findAllByOwnerIsNull();

    @Modifying
    @Query("update Subject subject set subject.owner = :owner where subject.owner is null")
    int assignUnownedSubjectsToOwner(@Param("owner") AppUser owner);
}
