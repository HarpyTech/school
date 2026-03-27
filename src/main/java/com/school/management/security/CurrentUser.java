package com.school.management.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * Convenience annotation to inject the currently authenticated {@link UserPrincipal}.
 * Usage: {@code public ResponseEntity<?> method(@CurrentUser UserPrincipal user)}
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
