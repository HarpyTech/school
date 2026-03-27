package com.school.management.user.domain;

/**
 * Lifecycle status for a user account.
 */
public enum UserStatus {
    PENDING_VERIFICATION,  // Registered but email not verified
    ACTIVE,                // Fully active account
    INACTIVE,              // Deactivated by admin
    SUSPENDED              // Suspended due to policy violation
}
