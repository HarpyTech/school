package com.school.management.user.application.dto.response;

public record AuthResponse(
        String accessToken,
        long expiresInMs,
        String refreshToken,
        UserResponse user
) {}
