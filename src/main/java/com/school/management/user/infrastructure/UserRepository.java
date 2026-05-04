package com.school.management.user.infrastructure;

import com.school.management.user.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByPasswordResetTokenAndDeletedFalse(String passwordResetToken);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
