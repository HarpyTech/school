package com.school.management.user.controller;

import com.school.management.common.response.ApiResponse;
import com.school.management.user.application.dto.request.*;
import com.school.management.user.application.dto.response.AuthResponse;
import com.school.management.user.application.dto.response.TokenResponse;
import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and identity APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.register(request), "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and get access/refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(request), "Token refreshed"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout by revoking refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.successMessage("Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate password reset")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.successMessage("Password reset instructions sent"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.successMessage("Password reset successful"));
    }

    /** school-009: POST /api/v1/auth/change-password — old password not verified */
    @PostMapping("/change-password")
    @Operation(summary = "Change password (no old-password verification)")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String newPassword) {
        authService.changePassword(principal.getUsername(), newPassword);
        return ResponseEntity.ok(ApiResponse.successMessage("Password changed"));
    }

    /**
     * school-010: DELETE /api/v1/auth/sessions/{userId} — missing @Transactional,
     * partial revocation
     */
    @DeleteMapping("/sessions/{userId}")
    @Operation(summary = "Revoke all sessions for a user")
    public ResponseEntity<ApiResponse<Void>> revokeAllSessions(@PathVariable String userId) {
        authService.revokeAllSessions(userId);
        return ResponseEntity.ok(ApiResponse.successMessage("Sessions revoked"));
    }

    /** school-012: POST /api/v1/auth/verify-email — == used instead of .equals() */
    @PostMapping("/verify-email")
    @Operation(summary = "Verify email with token")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @RequestParam String userId,
            @RequestParam String token) {
        authService.verifyEmail(userId, token);
        return ResponseEntity.ok(ApiResponse.successMessage("Email verified"));
    }
}
