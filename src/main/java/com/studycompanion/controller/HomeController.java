package com.studycompanion.controller;

import com.studycompanion.entity.Subject;
import com.studycompanion.service.SubjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class HomeController {

    private final SubjectService subjectService;

    public HomeController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public Subject createSubject(@RequestBody Subject subject, HttpSession session) {
        return subjectService.createSubject(subject, currentUserId(session));
    }

    @GetMapping
    public List<Subject> getAllSubjects(HttpSession session) {
        return subjectService.getAllSubjects(currentUserId(session));
    }

    @GetMapping("/{id}")
    public Subject getSubjectById(@PathVariable Long id, HttpSession session) {
        return subjectService.getSubjectById(id, currentUserId(session));
    }

    @PutMapping("/{id}")
    public Subject updateSubject(@PathVariable Long id, @RequestBody Subject subject, HttpSession session) {
        return subjectService.updateSubject(id, subject, currentUserId(session));
    }

    @DeleteMapping("/{id}")
    public String deleteSubject(@PathVariable Long id, HttpSession session) {
        subjectService.deleteSubject(id, currentUserId(session));
        return "Subject deleted";
    }

    private Long currentUserId(HttpSession session) {
        return ((Number) session.getAttribute("userId")).longValue();
    }
}
