package com.school.management.user.infrastructure;

import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByPasswordResetTokenAndDeletedFalse(String passwordResetToken);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM User u
            WHERE u.deleted = false
              AND (:schoolId IS NULL OR u.schoolId = :schoolId)
              AND (:status IS NULL OR u.status = :status)
              AND (
                    :search IS NULL OR :search = ''
                    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            """)
    Page<User> search(
            @Param("schoolId") String schoolId,
            @Param("status") UserStatus status,
            @Param("search") String search,
            Pageable pageable
    );
}
