package com.studycompanion.controller;

import com.studycompanion.dto.NoteRequest;
import com.studycompanion.entity.Note;
import com.studycompanion.service.NoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public Note createNote(@RequestBody NoteRequest request, HttpSession session) {
        return noteService.createNote(request, currentUserId(session));
    }

    @GetMapping
    public List<Note> getAllNotes(HttpSession session) {
        return noteService.getAllNotes(currentUserId(session));
    }

    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable Long id, HttpSession session) {
        return noteService.getNoteById(id, currentUserId(session));
    }

    @GetMapping("/subject/{subjectId}")
    public List<Note> getNotesBySubject(@PathVariable Long subjectId, HttpSession session) {
        return noteService.getNotesBySubjectId(subjectId, currentUserId(session));
    }

    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id, @RequestBody NoteRequest request, HttpSession session) {
        return noteService.updateNote(id, request, currentUserId(session));
    }

    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable Long id, HttpSession session) {
        noteService.deleteNote(id, currentUserId(session));
        return "Note deleted";
    }

    private Long currentUserId(HttpSession session) {
        return ((Number) session.getAttribute("userId")).longValue();
    }
}
