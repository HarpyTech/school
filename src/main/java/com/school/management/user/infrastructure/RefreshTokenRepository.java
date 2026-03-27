package com.school.management.user.infrastructure;

import com.school.management.user.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenAndDeletedFalse(String token);

    void deleteByUserId(String userId);
}
