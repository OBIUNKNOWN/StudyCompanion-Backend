package com.studycompanion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.Instant;

@Entity
@Table(name = "subject_progress")
public class SubjectProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subject_id", nullable = false, unique = true)
    private Subject subject;

    private int completedTopics;
    private int totalTopics;
    private Instant updatedAt;

    public SubjectProgress() {
    }

    public SubjectProgress(Subject subject, int completedTopics, int totalTopics) {
        this.subject = subject;
        this.completedTopics = completedTopics;
        this.totalTopics = totalTopics;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public int getCompletedTopics() { return completedTopics; }
    public void setCompletedTopics(int completedTopics) { this.completedTopics = completedTopics; }

    public int getTotalTopics() { return totalTopics; }
    public void setTotalTopics(int totalTopics) { this.totalTopics = totalTopics; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Transient
    public double getProgressPercentage() {
        if (totalTopics == 0) {
            return 0.0;
        }
        return Math.round((double) completedTopics / totalTopics * 10000.0) / 100.0;
    }
}
