package com.school.management.common.constants;

/**
 * Application-wide constants.
 */
public final class AppConstants {

    private AppConstants() {}

    // ─── Pagination Defaults ──────────────────────────────────────────────────
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIR = "desc";

    // ─── Security ────────────────────────────────────────────────────────────
    public static final String TOKEN_TYPE = "Bearer";
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // ─── Student ─────────────────────────────────────────────────────────────
    public static final String STUDENT_CODE_PREFIX = "STU";
    public static final String ADMISSION_CODE_PREFIX = "ADM";

    // ─── Roles ───────────────────────────────────────────────────────────────
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SCHOOL_ADMIN = "SCHOOL_ADMIN";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_PARENT = "PARENT";
}
