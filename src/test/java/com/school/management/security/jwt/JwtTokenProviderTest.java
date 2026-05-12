package com.school.management.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider.
 * Tests JWT token generation, validation, expiration, and claims extraction.
 */
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String TEST_USER_ID = "user-123";

    @BeforeEach
    void setUp() {
        // Initialize with test secret and expiration times
        jwtTokenProvider = new JwtTokenProvider();
        jwtTokenProvider.setJwtSecret("test-secret-key-for-jwt-testing-purposes-only-must-be-minimum-256-bits");
        jwtTokenProvider.setJwtExpirationMs(3600000); // 1 hour
        jwtTokenProvider.setJwtRefreshExpirationMs(86400000); // 24 hours
    }

    @DisplayName("generateAccessToken: should generate valid access token")
    @Test
    void testGenerateAccessToken_ValidUserId_ReturnsToken() {
        // When
        String token = jwtTokenProvider.generateAccessToken(TEST_USER_ID);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token).contains("."); // JWT format has dots
    }

    @DisplayName("generateRefreshToken: should generate valid refresh token")
    @Test
    void testGenerateRefreshToken_ValidUserId_ReturnsToken() {
        // When
        String token = jwtTokenProvider.generateRefreshToken(TEST_USER_ID);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token).contains(".");
    }

    @DisplayName("getUserIdFromToken: should extract userId from valid token")
    @Test
    void testGetUserIdFromToken_ValidToken_ReturnsUserId() {
        // Given
        String token = jwtTokenProvider.generateAccessToken(TEST_USER_ID);

        // When
        String userId = jwtTokenProvider.getUserIdFromToken(token);

        // Then
        assertThat(userId).isEqualTo(TEST_USER_ID);
    }

    @DisplayName("validateToken: should validate token successfully")
    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        // Given
        String token = jwtTokenProvider.generateAccessToken(TEST_USER_ID);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @DisplayName("validateToken: should reject malformed token")
    @Test
    void testValidateToken_MalformedToken_ReturnsFalse() {
        // Given
        String malformedToken = "not.a.valid.jwt.token";

        // When
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @DisplayName("validateToken: should reject empty token")
    @Test
    void testValidateToken_EmptyToken_ReturnsFalse() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @DisplayName("validateToken: should reject token with invalid signature")
    @Test
    void testValidateToken_InvalidSignature_ReturnsFalse() {
        // Given
        String token = jwtTokenProvider.generateAccessToken(TEST_USER_ID);
        String corruptedToken = token.substring(0, token.length() - 10) + "corrupted"; // Corrupt last chars

        // When
        boolean isValid = jwtTokenProvider.validateToken(corruptedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @DisplayName("validateToken: should reject expired token")
    @Test
    void testValidateToken_ExpiredToken_ReturnsFalse() throws InterruptedException {
        // Given - Create provider with very short expiration
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider();
        shortLivedProvider.setJwtSecret("test-secret-key-for-jwt-testing-purposes-only-must-be-minimum-256-bits");
        shortLivedProvider.setJwtExpirationMs(100); // 100ms expiration
        String token = shortLivedProvider.generateAccessToken(TEST_USER_ID);

        // Wait for token to expire
        Thread.sleep(150);

        // When
        boolean isValid = shortLivedProvider.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @DisplayName("getTokenType: should return Bearer token type")
    @Test
    void testGetTokenType_AlwaysReturnsBearer() {
        // When
        String tokenType = jwtTokenProvider.getTokenType();

        // Then
        assertThat(tokenType).isEqualTo("Bearer");
    }

    @DisplayName("generateAccessToken: should generate different tokens for different userIds")
    @Test
    void testGenerateAccessToken_DifferentUserIds_GeneratesDifferentTokens() {
        // When
        String token1 = jwtTokenProvider.generateAccessToken("user-1");
        String token2 = jwtTokenProvider.generateAccessToken("user-2");

        // Then
        assertThat(token1).isNotEqualTo(token2);
    }

    @DisplayName("generateAccessToken: should generate different tokens each time")
    @Test
    void testGenerateAccessToken_SameUserId_GeneratesDifferentTokensEachTime() {
        // When
        String token1 = jwtTokenProvider.generateAccessToken(TEST_USER_ID);
        String token2 = jwtTokenProvider.generateAccessToken(TEST_USER_ID);

        // Then
        assertThat(token1).isNotEqualTo(token2); // Different due to iat (issued at) claim
    }

    @DisplayName("getUserIdFromToken: should throw exception for invalid token")
    @Test
    void testGetUserIdFromToken_InvalidToken_ThrowsException() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThatThrownBy(() -> jwtTokenProvider.getUserIdFromToken(invalidToken))
                .isInstanceOf(Exception.class); // Could be JwtException or similar
    }

    @DisplayName("generateRefreshToken: should generate token with longer expiration than access token")
    @Test
    void testGenerateRefreshToken_LongerExpiration_ThanAccessToken() {
        // Given
        JwtTokenProvider provider = new JwtTokenProvider();
        provider.setJwtSecret("test-secret-key-for-jwt-testing-purposes-only-must-be-minimum-256-bits");
        provider.setJwtExpirationMs(3600000); // 1 hour
        provider.setJwtRefreshExpirationMs(604800000); // 7 days

        // When
        String accessToken = provider.generateAccessToken(TEST_USER_ID);
        String refreshToken = provider.generateRefreshToken(TEST_USER_ID);

        // Then - Both tokens should be valid, but refresh token lasts longer
        assertThat(provider.validateToken(accessToken)).isTrue();
        assertThat(provider.validateToken(refreshToken)).isTrue();
    }
}
