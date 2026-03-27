package com.school.management.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Role entity — each role maps to a {@link RoleName} enum value.
 */
@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_role_name", columnList = "name", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private RoleName name;

    @Column(name = "description", length = 255)
    private String description;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public Role(RoleName name) {
        this.name = name;
    }
}
