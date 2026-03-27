package com.school.management.user.infrastructure;

import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(RoleName name);
}
