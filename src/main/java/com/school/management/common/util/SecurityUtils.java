package com.school.management.common.util;

import com.school.management.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility for accessing the currently authenticated user's context.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static Optional<UserPrincipal> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public static String getCurrentUserId() {
        return getCurrentUser()
                .map(UserPrincipal::getId)
                .orElseThrow(() -> new IllegalStateException("No authenticated user in context"));
    }

    public static String getCurrentUserSchoolId() {
        return getCurrentUser()
                .map(UserPrincipal::getSchoolId)
                .orElse(null);
    }

    public static boolean isAdmin() {
        return getCurrentUser()
                .map(u -> u.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ADMIN")))
                .orElse(false);
    }
}
