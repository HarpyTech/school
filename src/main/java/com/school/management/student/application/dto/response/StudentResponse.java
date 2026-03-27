package com.school.management.student.application.dto.response;

import com.school.management.student.domain.StudentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentResponse(
        String id,
        String schoolId,
        String admissionNumber,
        String firstName,
        String lastName,
        LocalDate admissionDate,
        String currentGrade,
        String section,
        StudentStatus status,
        String parentId,
        String parentName,
        String city,
        String state,
        LocalDateTime createdAt
) {}
