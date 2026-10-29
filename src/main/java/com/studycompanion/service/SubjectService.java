package com.studycompanion.service;

import com.studycompanion.entity.AppUser;
import com.studycompanion.entity.Subject;
import com.studycompanion.exception.DuplicateSubjectException;
import com.studycompanion.exception.NotFoundException;
import com.studycompanion.repository.AppUserRepository;
import com.studycompanion.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final AppUserRepository userRepository;

    public SubjectService(SubjectRepository subjectRepository, AppUserRepository userRepository) {
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    public Subject createSubject(Subject subject, Long ownerId) {
        String name = normalizeName(subject.getName());
        if (subjectRepository.existsByNameIgnoreCaseAndOwner_Id(name, ownerId)) {
            throw new DuplicateSubjectException("Subject already exists: " + name);
        }
        AppUser owner = getOwner(ownerId);
        subject.setName(name);
        subject.setOwner(owner);
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects(Long ownerId) {
        return subjectRepository.findAllByOwner_Id(ownerId);
    }

    public Subject getSubjectById(Long id, Long ownerId) {
        return subjectRepository.findByIdAndOwner_Id(id, ownerId)
                .orElseThrow(() -> new NotFoundException("Subject not found with id " + id));
    }

    public Subject updateSubject(Long id, Subject updated, Long ownerId) {
        Subject existing = getSubjectById(id, ownerId);
        String name = normalizeName(updated.getName());
        if (subjectRepository.existsByNameIgnoreCaseAndOwner_IdAndIdNot(name, ownerId, id)) {
            throw new DuplicateSubjectException("Subject already exists: " + name);
        }
        existing.setName(name);
        existing.setDescription(updated.getDescription());
        return subjectRepository.save(existing);
    }

    public void deleteSubject(Long id, Long ownerId) {
        subjectRepository.delete(getSubjectById(id, ownerId));
    }

    private AppUser getOwner(Long ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Subject name is required");
        }
        return name.trim();
    }
}
