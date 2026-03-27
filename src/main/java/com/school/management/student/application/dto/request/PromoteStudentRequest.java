package com.school.management.student.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PromoteStudentRequest(
        @NotBlank String nextGrade,
        String nextSection
) {}
