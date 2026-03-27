package com.school.management.user.application.service;

import com.school.management.common.constants.AppConstants;
import com.school.management.common.event.EventPublisher;
import com.school.management.common.exception.BusinessException;
import com.school.management.common.exception.DuplicateResourceException;
import com.school.management.common.exception.ResourceNotFoundException;
import com.school.management.security.UserPrincipal;
import com.school.management.security.jwt.JwtTokenProvider;
import com.school.management.user.application.dto.request.*;
import com.school.management.user.application.dto.response.AuthResponse;
import com.school.management.user.application.dto.response.TokenResponse;
import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.application.mapper.UserMapper;
import com.school.management.user.domain.*;
import com.school.management.user.domain.event.UserRegisteredEvent;
import com.school.management.user.infrastructure.RefreshTokenRepository;
import com.school.management.user.infrastructure.RoleRepository;
import com.school.management.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EventPublisher eventPublisher;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("User", "username", request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User", "email", request.email());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phone());
        user.setSchoolId(request.schoolId());
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        Set<RoleName> requestedRoles = request.roles() == null || request.roles().isEmpty()
                ? Set.of(RoleName.STUDENT)
                : request.roles();

        Set<Role> roles = requestedRoles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BusinessException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        User saved = userRepository.save(user);

        eventPublisher.publish(AppConstants.TOPIC_USER_REGISTERED, saved.getId(),
                UserRegisteredEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType("USER_REGISTERED")
                        .occurredOn(LocalDateTime.now())
                        .userId(saved.getId())
                        .schoolId(saved.getSchoolId())
                        .email(saved.getEmail())
                        .username(saved.getUsername())
                        .roles(saved.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                        .build());

        return userMapper.toResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshToken = createRefreshToken(principal.getId());

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));
        user.setLastLoginAt(LocalDateTime.now());

        return new AuthResponse(
                accessToken,
                tokenProvider.getExpirationMs(),
                refreshToken,
                userMapper.toResponse(user)
        );
    }

    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndDeletedFalse(request.refreshToken())
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new BusinessException("Refresh token is expired or revoked");
        }

        UserPrincipal principal = UserPrincipal.create(refreshToken.getUser());
        String accessToken = tokenProvider.generateAccessToken(principal);

        return new TokenResponse(accessToken, tokenProvider.getExpirationMs());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByTokenAndDeletedFalse(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(2));

        userRepository.save(user);

        // Publish notification event; template service can send email asynchronously.
        eventPublisher.publish(AppConstants.TOPIC_NOTIFICATION_SEND, user.getId(), java.util.Map.of(
                "type", "PASSWORD_RESET",
                "email", user.getEmail(),
                "token", token,
                "userId", user.getId()
        ));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetTokenAndDeletedFalse(request.token())
            .orElseThrow(() -> new BusinessException("Invalid reset token"));

        if (user.getPasswordResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getPasswordResetTokenExpiry())) {
            throw new BusinessException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public String createRefreshToken(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}
