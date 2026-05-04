package com.school.management.user.domain;

import com.school.management.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Core User document for authentication and identity.
 * Roles drive authorization; schoolId enables multi-tenancy.
 */
@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Indexed(unique = true)
    @Field("username")
    private String username;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("phone_number")
    private String phoneNumber;

    @Field("profile_picture")
    private String profilePicture;

    @Field("school_id")
    private String schoolId; // null for ADMIN users; set for all tenant-scoped users

    @Field("status")
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Field("email_verified")
    private boolean emailVerified = false;

    @Field("email_verification_token")
    private String emailVerificationToken;

    @Field("email_verification_token_expiry")
    private LocalDateTime emailVerificationTokenExpiry;

    @Field("password_reset_token")
    private String passwordResetToken;

    @Field("password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    @Field("last_login_at")
    private LocalDateTime lastLoginAt;

    @Field("roles")
    private Set<RoleName> roles = new HashSet<>();

    public void addRole(RoleName roleName) {
        this.roles.add(roleName);
    }

    public boolean hasRole(RoleName roleName) {
        if (roleName == null || roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.contains(roleName);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
