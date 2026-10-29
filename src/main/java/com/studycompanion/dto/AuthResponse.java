package com.studycompanion.dto;

public record AuthResponse(Long id, String displayName, String email, boolean authenticated) {
}
