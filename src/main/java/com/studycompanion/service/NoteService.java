package com.studycompanion.service;

import com.studycompanion.dto.NoteRequest;
import com.studycompanion.entity.Note;
import com.studycompanion.entity.Subject;
import com.studycompanion.exception.NotFoundException;
import com.studycompanion.repository.NoteRepository;
import com.studycompanion.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final SubjectRepository subjectRepository;

    public NoteService(NoteRepository noteRepository, SubjectRepository subjectRepository) {
        this.noteRepository = noteRepository;
        this.subjectRepository = subjectRepository;
    }

    public Note createNote(NoteRequest request, Long ownerId) {
        validate(request);
        Subject subject = getSubject(request.subjectId(), ownerId);
        Note note = new Note(request.title().trim(), request.content(), subject);
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes(Long ownerId) {
        return noteRepository.findAllBySubject_Owner_Id(ownerId);
    }

    public Note getNoteById(Long id, Long ownerId) {
        return noteRepository.findByIdAndSubject_Owner_Id(id, ownerId)
                .orElseThrow(() -> new NotFoundException("Note not found with id " + id));
    }

    public List<Note> getNotesBySubjectId(Long subjectId, Long ownerId) {
        getSubject(subjectId, ownerId);
        return noteRepository.findAllBySubject_IdAndSubject_Owner_Id(subjectId, ownerId);
    }

    public Note updateNote(Long id, NoteRequest request, Long ownerId) {
        validate(request);
        Note note = getNoteById(id, ownerId);
        Subject subject = getSubject(request.subjectId(), ownerId);

        note.setTitle(request.title().trim());
        note.setContent(request.content());
        note.setSubject(subject);
        return noteRepository.save(note);
    }

    public void deleteNote(Long id, Long ownerId) {
        noteRepository.delete(getNoteById(id, ownerId));
    }

    private Subject getSubject(Long subjectId, Long ownerId) {
        if (subjectId == null) {
            throw new IllegalArgumentException("subjectId is required");
        }
        return subjectRepository.findByIdAndOwner_Id(subjectId, ownerId)
                .orElseThrow(() -> new NotFoundException("Subject not found with id " + subjectId));
    }

    private void validate(NoteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Note body is required");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("Note title is required");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Note content is required");
        }
    }
}
