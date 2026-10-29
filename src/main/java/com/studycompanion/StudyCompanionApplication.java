package com.studycompanion;

import com.studycompanion.entity.Subject;
import com.studycompanion.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StudyCompanionApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyCompanionApplication.class, args);
    }


}