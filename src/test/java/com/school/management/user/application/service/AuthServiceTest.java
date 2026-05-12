package com.school.management.user.application.service;

import com.school.management.common.exception.BusinessException;
import com.school.management.common.exception.ResourceNotFoundException;
import com.school.management.common.test.BaseServiceTest;
import com.school.management.common.test.TestDataFactory;
import com.school.management.user.application.dto.request.LoginRequest;
import com.school.management.user.application.dto.request.RegisterRequest;
import com.school.management.user.application.dto.request.RefreshTokenRequest;
import com.school.management.user.application.dto.request.ForgotPasswordRequest;
import com.school.management.user.application.dto.request.ResetPasswordRequest;
import com.school.management.user.application.dto.response.AuthResponse;
import com.school.management.user.application.dto.response.TokenResponse;
import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.application.mapper.UserMapper;
import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.RefreshToken;
import com.school.management.user.infrastructure.UserRepository;
import com.school.management.user.infrastructure.RefreshTokenRepository;
import com.school.management.user.infrastructure.RoleRepository;
import com.school.management.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 * Tests authentication, registration, token refresh, password reset, and email
 * verification.
 */
@DisplayName("AuthService Tests")
class AuthServiceTest extends BaseServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        TestDataFactory.reset();
        testUser = TestDataFactory.createTestUser("john_doe", "john@test.com", "John Doe", "password123");
        testUser.setId("user-123");
        testUser.setStatus(UserStatus.ACTIVE);
        testUserResponse = new UserResponse();
        testUserResponse.setId("user-123");
        testUserResponse.setUsername("john_doe");
    }

    @DisplayName("register: should create new user successfully")
    @Test
    void testRegister_NewUser_Success() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@test.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("User");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse result = authService.register(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john_doe");
        verify(userRepository).save(any(User.class));
    }

    @DisplayName("register: should throw exception for duplicate email")
    @Test
    void testRegister_DuplicateEmail_ThrowsException() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@test.com");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(Exception.class);
    }

    @DisplayName("register: should throw exception for duplicate username")
    @Test
    void testRegister_DuplicateUsername_ThrowsException() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing_user");
        request.setEmail("new@test.com");
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(Exception.class);
    }

    @DisplayName("login: should authenticate user and return tokens")
    @Test
    void testLogin_ValidCredentials_ReturnsAuthResponse() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("john_doe");
        request.setPassword("password123");

        when(userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername()))
                .thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUser.getUsername(), null));
        when(jwtTokenProvider.generateAccessToken(testUser.getId())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(testUser.getId())).thenReturn("refresh-token");
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        AuthResponse result = authService.login(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(testUser.getLastLoginAt()).isNotNull();
    }

    @DisplayName("login: should throw exception for invalid credentials")
    @Test
    void testLogin_InvalidCredentials_ThrowsException() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("john_doe");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @DisplayName("refreshToken: should generate new access token")
    @Test
    void testRefreshToken_ValidToken_ReturnsNewAccessToken() {
        // Given
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId("user-123");
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshTokenValue);

        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
        when(jwtTokenProvider.generateAccessToken("user-123")).thenReturn("new-access-token");

        // When
        TokenResponse result = authService.refreshToken(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
    }

    @DisplayName("refreshToken: should throw exception for expired token")
    @Test
    void testRefreshToken_ExpiredToken_ThrowsException() {
        // Given
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(LocalDateTime.now().minusDays(1)); // Expired

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshTokenValue);

        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("refreshToken: should throw exception for revoked token")
    @Test
    void testRefreshToken_RevokedToken_ThrowsException() {
        // Given
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRevoked(true);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshTokenValue);

        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("logout: should revoke refresh token")
    @Test
    void testLogout_ValidToken_Revoked() {
        // Given
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setRevoked(false);

        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        // When
        authService.logout(refreshTokenValue);

        // Then
        assertThat(refreshToken.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(refreshToken);
    }

    @DisplayName("forgotPassword: should generate reset token")
    @Test
    void testForgotPassword_ValidEmail_TokenGenerated() {
        // Given
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("john@test.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        authService.forgotPassword(request);

        // Then
        assertThat(testUser.getPasswordResetToken()).isNotNull();
        assertThat(testUser.getPasswordResetTokenExpiry()).isNotNull();
        verify(userRepository).save(testUser);
    }

    @DisplayName("forgotPassword: should throw exception for non-existent email")
    @Test
    void testForgotPassword_NonExistentEmail_ThrowsException() {
        // Given
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("nonexistent@test.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.forgotPassword(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("resetPassword: should update password with valid token")
    @Test
    void testResetPassword_ValidToken_PasswordUpdated() {
        // Given
        String resetToken = UUID.randomUUID().toString();
        testUser.setPasswordResetToken(resetToken);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(2));

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(resetToken);
        request.setNewPassword("newpassword123");

        when(userRepository.findByPasswordResetTokenAndDeletedFalse(resetToken))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encoded-new-password");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        authService.resetPassword(request);

        // Then
        assertThat(testUser.getPassword()).isEqualTo("encoded-new-password");
        assertThat(testUser.getPasswordResetToken()).isNull();
        assertThat(testUser.getPasswordResetTokenExpiry()).isNull();
    }

    @DisplayName("resetPassword: should throw exception for expired token")
    @Test
    void testResetPassword_ExpiredToken_ThrowsException() {
        // Given
        String resetToken = UUID.randomUUID().toString();
        testUser.setPasswordResetToken(resetToken);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().minusHours(1)); // Expired

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(resetToken);
        request.setNewPassword("newpassword123");

        when(userRepository.findByPasswordResetTokenAndDeletedFalse(resetToken))
                .thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("changePassword: should update password without old password verification (BUG-9)")
    @Test
    void testChangePassword_NoOldPasswordVerification_Success() {
        // Given
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded-new-password");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        authService.changePassword("user-123", "newpassword123");

        // Then
        // BUG-9: This should fail because old password is not verified, but it succeeds
        assertThat(testUser.getPassword()).isEqualTo("encoded-new-password");
        verify(userRepository).save(testUser);
    }

    @DisplayName("revokeAllSessions: should revoke all refresh tokens for user")
    @Test
    void testRevokeAllSessions_ValidUser_SessionsRevoked() {
        // Given
        RefreshToken token1 = new RefreshToken();
        token1.setUserId("user-123");
        token1.setRevoked(false);

        RefreshToken token2 = new RefreshToken();
        token2.setUserId("user-123");
        token2.setRevoked(false);

        when(refreshTokenRepository.findByUserId("user-123"))
                .thenReturn(java.util.List.of(token1, token2));
        when(refreshTokenRepository.saveAll(any())).thenReturn(java.util.List.of(token1, token2));

        // When
        authService.revokeAllSessions("user-123");

        // Then
        verify(refreshTokenRepository).saveAll(any());
    }

    @DisplayName("revokeAllSessions: should throw exception when user not found")
    @Test
    void testRevokeAllSessions_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.revokeAllSessions("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("verifyEmail: should verify email and activate user")
    @Test
    void testVerifyEmail_ValidToken_EmailVerified() {
        // Given
        String verificationToken = UUID.randomUUID().toString();
        testUser.setEmailVerificationToken(verificationToken);
        testUser.setEmailVerified(false);
        testUser.setStatus(UserStatus.PENDING_VERIFICATION);

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        // Note: BUG-12 uses == instead of .equals(), so this test might fail in actual
        // implementation
        authService.verifyEmail("user-123", verificationToken);

        // Then - This assertion documents the expected behavior (not current buggy
        // behavior)
        assertThat(testUser.isEmailVerified()).isTrue();
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("verifyEmail: should throw exception for invalid token")
    @Test
    void testVerifyEmail_InvalidToken_ThrowsException() {
        // Given
        String correctToken = UUID.randomUUID().toString();
        testUser.setEmailVerificationToken(correctToken);

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.verifyEmail("user-123", "wrong-token"))
                .isInstanceOf(BusinessException.class);
    }
}
