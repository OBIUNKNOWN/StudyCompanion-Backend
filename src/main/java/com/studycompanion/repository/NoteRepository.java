package com.studycompanion.repository;

import com.studycompanion.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllBySubject_Owner_Id(Long ownerId);

    List<Note> findAllBySubject_IdAndSubject_Owner_Id(Long subjectId, Long ownerId);

    Optional<Note> findByIdAndSubject_Owner_Id(Long noteId, Long ownerId);
}
