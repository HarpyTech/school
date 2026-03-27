package com.school.management.user.application.dto.response;

import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.UserStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String profilePicture,
        String schoolId,
        UserStatus status,
        boolean emailVerified,
        LocalDateTime lastLoginAt,
        Set<RoleName> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
