package com.school.management.common.audit;

import com.school.management.security.UserPrincipal;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Provides the currently authenticated user ID to Spring Data JPA auditing.
 */
@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of("system");
        }

        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal.getId());
        }

        return Optional.of(authentication.getName());
    }
}
