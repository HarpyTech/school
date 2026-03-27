package com.school.management.user.application.service;

import com.school.management.common.exception.DuplicateResourceException;
import com.school.management.security.jwt.JwtTokenProvider;
import com.school.management.user.application.dto.request.RegisterRequest;
import com.school.management.user.application.mapper.UserMapper;
import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import com.school.management.user.infrastructure.RefreshTokenRepository;
import com.school.management.user.infrastructure.RoleRepository;
import com.school.management.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest(
                "john",
                "john@school.com",
                "Password@123",
                "John",
                "Doe",
                "1234567890",
                "school-1",
                Set.of(RoleName.STUDENT)
        );
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    @Test
    void register_shouldThrowWhenRoleNotFound() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@school.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.STUDENT)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }
}
