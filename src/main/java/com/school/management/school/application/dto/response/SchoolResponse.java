package com.school.management.school.application.dto.response;

import java.time.LocalDateTime;

public record SchoolResponse(
        String id,
        String name,
        String code,
        String email,
        String phone,
        String city,
        String state,
        String country,
        boolean active,
        LocalDateTime createdAt
) {}
