package com.studycompanion.config;

import com.studycompanion.entity.AppUser;
import com.studycompanion.repository.AppUserRepository;
import com.studycompanion.repository.SubjectRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ExistingSubjectOwnershipMigration implements ApplicationRunner {

    private final AppUserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public ExistingSubjectOwnershipMigration(AppUserRepository userRepository, SubjectRepository subjectRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<AppUser> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        if (!users.isEmpty()) {
            subjectRepository.assignUnownedSubjectsToOwner(users.get(0));
        }
    }
}
