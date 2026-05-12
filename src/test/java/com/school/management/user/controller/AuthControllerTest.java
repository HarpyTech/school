package com.school.management.user.controller;

import com.school.management.common.test.BaseControllerTest;
import com.school.management.common.test.TestDataFactory;
import com.school.management.user.application.dto.request.LoginRequest;
import com.school.management.user.application.dto.request.RegisterRequest;
import com.school.management.user.application.dto.request.RefreshTokenRequest;
import com.school.management.user.application.dto.response.AuthResponse;
import com.school.management.user.application.dto.response.TokenResponse;
import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.application.service.AuthService;
import com.school.management.user.domain.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController.
 * Tests authentication endpoints: register, login, refresh, logout, password
 * reset, etc.
 */
@DisplayName("AuthController Tests")
class AuthControllerTest extends BaseControllerTest {

    @MockBean
    private AuthService authService;

    private UserResponse testUserResponse;
    private AuthResponse testAuthResponse;

    @BeforeEach
    void setUp() {
        TestDataFactory.reset();

        testUserResponse = new UserResponse();
        testUserResponse.setId("user-123");
        testUserResponse.setUsername("john_doe");
        testUserResponse.setEmail("john@test.com");
        testUserResponse.setFirstName("John");
        testUserResponse.setLastName("Doe");

        testAuthResponse = new AuthResponse();
        testAuthResponse.setAccessToken("access-token-value");
        testAuthResponse.setRefreshToken("refresh-token-value");
        testAuthResponse.setUser(testUserResponse);
    }

    @DisplayName("POST /api/v1/auth/register: should register new user")
    @Test
    @WithAnonymousUser
    void testRegister_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@test.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("User");

        when(authService.register(any(RegisterRequest.class))).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @DisplayName("POST /api/v1/auth/register: should validate required fields")
    @Test
    @WithAnonymousUser
    void testRegister_MissingEmail_ReturnsBadRequest() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        // Missing email
        request.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /api/v1/auth/login: should authenticate user and return tokens")
    @Test
    @WithAnonymousUser
    void testLogin_ValidCredentials_ReturnsTokens() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("john_doe");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class))).thenReturn(testAuthResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-value"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-value"))
                .andExpect(jsonPath("$.user.username").value("john_doe"));
    }

    @DisplayName("POST /api/v1/auth/login: should reject invalid credentials")
    @Test
    @WithAnonymousUser
    void testLogin_InvalidCredentials_ReturnsForbidden() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("john_doe");
        request.setPassword("wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("POST /api/v1/auth/refresh: should return new access token")
    @Test
    void testRefreshToken_ValidToken_ReturnsNewAccessToken() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token-value");

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("new-access-token");
        tokenResponse.setTokenType("Bearer");

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(tokenResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @DisplayName("POST /api/v1/auth/logout: should revoke refresh token")
    @Test
    void testLogout_ValidToken_ReturnsSuccess() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token-value");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
                .andExpect(status().isOk());
    }

    @DisplayName("POST /api/v1/auth/forgot-password: should send password reset email")
    @Test
    @WithAnonymousUser
    void testForgotPassword_ValidEmail_ReturnSuccess() throws Exception {
        // Given - no request body needed for forgot password with email param
        // When & Then
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .param("email", "john@test.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("POST /api/v1/auth/reset-password: should reset password with valid token")
    @Test
    @WithAnonymousUser
    void testResetPassword_ValidToken_ReturnsSuccess() throws Exception {
        // Given - password reset with token and new password
        // When & Then
        mockMvc.perform(post("/api/v1/auth/reset-password")
                .param("token", "reset-token-123")
                .param("newPassword", "newpassword123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("POST /api/v1/auth/change-password: should change password for authenticated user")
    @Test
    void testChangePassword_ValidRequest_ReturnsSuccess() throws Exception {
        // Given - requires authentication
        // When & Then
        mockMvc.perform(post("/api/v1/auth/change-password")
                .param("newPassword", "newpassword123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("DELETE /api/v1/auth/sessions/{userId}: should revoke all sessions")
    @Test
    void testRevokeAllSessions_ValidUser_ReturnsSuccess() throws Exception {
        // Given - ADMIN only
        // When & Then
        mockMvc.perform(delete("/api/v1/auth/sessions/user-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("POST /api/v1/auth/verify-email: should verify email with token")
    @Test
    @WithAnonymousUser
    void testVerifyEmail_ValidToken_ReturnsSuccess() throws Exception {
        // Given
        // When & Then
        mockMvc.perform(post("/api/v1/auth/verify-email")
                .param("userId", "user-123")
                .param("token", "verification-token-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
