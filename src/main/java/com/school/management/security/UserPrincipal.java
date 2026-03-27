package com.school.management.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.school.management.user.domain.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring Security UserDetails implementation backed by our User domain object.
 */
@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

    private final String id;
    private final String username;
    private final String email;
    private final String schoolId;
    private final UserStatus status;
    private final boolean emailVerified;

    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public UserPrincipal(String id, String username, String email, String password,
                         String schoolId, UserStatus status, boolean emailVerified,
                         Collection<? extends GrantedAuthority> authorities,
                         Map<String, Object> attributes) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.schoolId = schoolId;
        this.status = status;
        this.emailVerified = emailVerified;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    public static UserPrincipal create(com.school.management.user.domain.User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getSchoolId(),
                user.getStatus(),
                user.isEmailVerified(),
                authorities,
                Map.of()
        );
    }

    public static UserPrincipal create(com.school.management.user.domain.User user, Map<String, Object> attributes) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getSchoolId(),
                user.getStatus(),
                user.isEmailVerified(),
                authorities,
                attributes == null ? Map.of() : attributes
        );
    }

    @Override
    public String getName() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
