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

    // ─── Cache Names ─────────────────────────────────────────────────────────
    public static final String CACHE_STUDENTS = "students";
    public static final String CACHE_SCHOOLS = "schools";
    public static final String CACHE_USERS = "users";
    public static final String CACHE_COURSES = "courses";

    // ─── Kafka Topics ────────────────────────────────────────────────────────
    public static final String TOPIC_USER_REGISTERED = "user.registered";
    public static final String TOPIC_STUDENT_ADMITTED = "student.admitted";
    public static final String TOPIC_STUDENT_PROMOTED = "student.promoted";
    public static final String TOPIC_PAYMENT_PROCESSED = "payment.processed";
    public static final String TOPIC_NOTIFICATION_SEND = "notification.send";
    public static final String TOPIC_AUDIT_LOG = "audit.log";

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
