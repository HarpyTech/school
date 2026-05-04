package com.school.management.user.infrastructure;

import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(RoleName name);
}
