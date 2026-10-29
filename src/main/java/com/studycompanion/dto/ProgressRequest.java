package com.studycompanion.dto;

public record ProgressRequest(Long subjectId, Integer completedTopics, Integer totalTopics) {
}
