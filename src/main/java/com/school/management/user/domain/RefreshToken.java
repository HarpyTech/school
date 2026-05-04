package com.school.management.user.domain;

import com.school.management.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "refresh_tokens")
@Getter
@Setter
public class RefreshToken extends BaseEntity {

    @Indexed(unique = true)
    @Field("token")
    private String token;

    @Indexed
    @Field("user_id")
    private String userId;

    @Indexed
    @Field("expires_at")
    private LocalDateTime expiresAt;

    @Field("revoked")
    private boolean revoked = false;

    public boolean isExpired() {
        // BUG-11: Off-by-one; tokens are treated as expired 1 minute before actual
        // expiry
        return LocalDateTime.now().isAfter(expiresAt.minusMinutes(1));
    }
}
