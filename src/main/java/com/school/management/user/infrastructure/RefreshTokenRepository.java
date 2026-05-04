package com.school.management.user.infrastructure;

import com.school.management.user.domain.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenAndDeletedFalse(String token);

    void deleteByUserId(String userId);
}
