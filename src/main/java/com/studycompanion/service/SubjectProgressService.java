package com.studycompanion.service;

import com.studycompanion.dto.ProgressRequest;
import com.studycompanion.entity.Subject;
import com.studycompanion.entity.SubjectProgress;
import com.studycompanion.exception.DuplicateProgressException;
import com.studycompanion.exception.NotFoundException;
import com.studycompanion.repository.SubjectProgressRepository;
import com.studycompanion.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SubjectProgressService {

    private final SubjectProgressRepository progressRepository;
    private final SubjectRepository subjectRepository;

    public SubjectProgressService(SubjectProgressRepository progressRepository, SubjectRepository subjectRepository) {
        this.progressRepository = progressRepository;
        this.subjectRepository = subjectRepository;
    }

    public SubjectProgress createProgress(ProgressRequest request, Long ownerId) {
        validate(request);
        Subject subject = getSubject(request.subjectId(), ownerId);
        if (progressRepository.existsBySubject_IdAndSubject_Owner_Id(request.subjectId(), ownerId)) {
            throw new DuplicateProgressException("Progress already exists for subject: " + subject.getName());
        }
        SubjectProgress progress = new SubjectProgress(subject, request.completedTopics(), request.totalTopics());
        return progressRepository.save(progress);
    }

    public List<SubjectProgress> getAllProgress(Long ownerId) {
        return progressRepository.findAllBySubject_Owner_Id(ownerId);
    }

    public SubjectProgress getProgressBySubjectId(Long subjectId, Long ownerId) {
        getSubject(subjectId, ownerId);
        return progressRepository.findBySubject_IdAndSubject_Owner_Id(subjectId, ownerId)
                .orElseThrow(() -> new NotFoundException("Progress not found for subject id " + subjectId));
    }

    public SubjectProgress updateProgress(Long subjectId, ProgressRequest request, Long ownerId) {
        validate(request);
        if (!subjectId.equals(request.subjectId())) {
            throw new IllegalArgumentException("URL subjectId must match the body subjectId");
        }
        SubjectProgress progress = getProgressBySubjectId(subjectId, ownerId);
        progress.setCompletedTopics(request.completedTopics());
        progress.setTotalTopics(request.totalTopics());
        progress.setUpdatedAt(Instant.now());
        return progressRepository.save(progress);
    }

    public void deleteProgress(Long subjectId, Long ownerId) {
        progressRepository.delete(getProgressBySubjectId(subjectId, ownerId));
    }

    private Subject getSubject(Long subjectId, Long ownerId) {
        if (subjectId == null) {
            throw new IllegalArgumentException("subjectId is required");
        }
        return subjectRepository.findByIdAndOwner_Id(subjectId, ownerId)
                .orElseThrow(() -> new NotFoundException("Subject not found with id " + subjectId));
    }

    private void validate(ProgressRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Progress body is required");
        }
        if (request.subjectId() == null) {
            throw new IllegalArgumentException("subjectId is required");
        }
        if (request.completedTopics() == null || request.totalTopics() == null) {
            throw new IllegalArgumentException("completedTopics and totalTopics are required");
        }
        if (request.totalTopics() <= 0) {
            throw new IllegalArgumentException("totalTopics must be greater than zero");
        }
        if (request.completedTopics() < 0 || request.completedTopics() > request.totalTopics()) {
            throw new IllegalArgumentException("completedTopics must be between zero and totalTopics");
        }
    }
}
