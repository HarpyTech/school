package com.school.management.student.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateStudentRequest(
        @NotBlank String schoolId,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        String gender,
        LocalDate dateOfBirth,
        @NotNull LocalDate admissionDate,
        @NotBlank String currentGrade,
        String section,
        String street,
        String city,
        String state,
        String country,
        String zipCode,
        String documentsJson,
        @NotBlank String parentFirstName,
        @NotBlank String parentLastName,
        String parentEmail,
        String parentPhone,
        String relationship
) {}
