package com.school.management.user.application.dto.response;

public record TokenResponse(
        String accessToken,
        long expiresInMs
) {}
