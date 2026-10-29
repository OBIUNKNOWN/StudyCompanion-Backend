package com.studycompanion.controller;

import com.studycompanion.dto.ProgressRequest;
import com.studycompanion.entity.SubjectProgress;
import com.studycompanion.service.SubjectProgressService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class SubjectProgressController {

    private final SubjectProgressService progressService;

    public SubjectProgressController(SubjectProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping
    public SubjectProgress createProgress(@RequestBody ProgressRequest request, HttpSession session) {
        return progressService.createProgress(request, currentUserId(session));
    }

    @GetMapping
    public List<SubjectProgress> getAllProgress(HttpSession session) {
        return progressService.getAllProgress(currentUserId(session));
    }

    @GetMapping("/subject/{subjectId}")
    public SubjectProgress getProgressBySubject(@PathVariable Long subjectId, HttpSession session) {
        return progressService.getProgressBySubjectId(subjectId, currentUserId(session));
    }

    @PutMapping("/subject/{subjectId}")
    public SubjectProgress updateProgress(@PathVariable Long subjectId, @RequestBody ProgressRequest request, HttpSession session) {
        return progressService.updateProgress(subjectId, request, currentUserId(session));
    }

    @DeleteMapping("/subject/{subjectId}")
    public String deleteProgress(@PathVariable Long subjectId, HttpSession session) {
        progressService.deleteProgress(subjectId, currentUserId(session));
        return "Progress deleted";
    }

    private Long currentUserId(HttpSession session) {
        return ((Number) session.getAttribute("userId")).longValue();
    }
}
