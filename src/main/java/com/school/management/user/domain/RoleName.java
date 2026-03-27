package com.school.management.user.domain;

/**
 * All supported roles in the system.
 */
public enum RoleName {
    ADMIN,          // Platform-level super admin
    SCHOOL_ADMIN,   // Admin of a specific school
    TEACHER,        // Teaching staff
    STUDENT,        // Enrolled student
    PARENT          // Parent/Guardian of a student
}
