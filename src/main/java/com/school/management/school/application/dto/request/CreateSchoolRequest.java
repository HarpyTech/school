package com.school.management.school.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSchoolRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 30) String code,
        @Size(max = 150) String email,
        @Size(max = 25) String phone,
        String street,
        String city,
        String state,
        String country,
        String zipCode
) {}
