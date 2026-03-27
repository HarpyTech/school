package com.school.management.user.domain;

import com.school.management.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Core User entity for authentication and identity.
 * Roles drive authorization; schoolId enables multi-tenancy.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_username", columnList = "username", unique = true),
        @Index(name = "idx_user_school", columnList = "school_id"),
        @Index(name = "idx_user_status", columnList = "status")
})
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "username", length = 100, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @Column(name = "school_id", length = 36)
    private String schoolId;  // null for ADMIN users; set for all tenant-scoped users

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "email_verification_token", length = 100)
    private String emailVerificationToken;

    @Column(name = "email_verification_token_expiry")
    private LocalDateTime emailVerificationTokenExpiry;

    @Column(name = "password_reset_token", length = 100)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
