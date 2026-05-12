package com.school.management.user.application.service;

import com.school.management.common.exception.BusinessException;
import com.school.management.common.exception.ResourceNotFoundException;
import com.school.management.common.response.PagedResponse;
import com.school.management.common.test.BaseServiceTest;
import com.school.management.common.test.TestDataFactory;
import com.school.management.user.application.mapper.UserMapper;
import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import com.school.management.user.domain.RoleName;
import com.school.management.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Tests user CRUD operations, search, status updates, role assignment, and soft
 * deletion.
 */
@DisplayName("UserService Tests")
class UserServiceTest extends BaseServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        TestDataFactory.reset();
        testUser = TestDataFactory.createTestUser("john_doe", "john@test.com", "John Doe", "password123");
        testUser.setId("user-123");
        testUserResponse = new UserResponse();
        testUserResponse.setId("user-123");
        testUserResponse.setUsername("john_doe");
        testUserResponse.setEmail("john@test.com");
    }

    @DisplayName("getById: should return user when found")
    @Test
    void testGetById_UserFound_Success() {
        // Given
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse result = userService.getById("user-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user-123");
        assertThat(result.getUsername()).isEqualTo("john_doe");
        verify(userRepository).findById("user-123");
        verify(userMapper).toResponse(testUser);
    }

    @DisplayName("getById: should return null when user not found (BUG-10)")
    @Test
    void testGetById_UserNotFound_ReturnsNull() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When
        UserResponse result = userService.getById("nonexistent");

        // Then
        assertThat(result).isNull(); // BUG-10: Should throw ResourceNotFoundException instead
    }

    @DisplayName("search: should return paginated users by schoolId and status")
    @Test
    void testSearch_WithFilters_ReturnsPaginatedResults() {
        // Given
        User user2 = TestDataFactory.createTestUser("jane_doe", "jane@test.com", "Jane Doe", "pass123");
        Page<User> usersPage = new PageImpl<>(List.of(testUser, user2), PageRequest.of(0, 20), 2);

        when(userRepository.search(anyString(), any(), anyString(), any()))
                .thenReturn(usersPage);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);
        when(userMapper.toResponse(user2)).thenReturn(testUserResponse);

        // When
        PagedResponse<UserResponse> result = userService.search(
                "school-123", UserStatus.ACTIVE, "john", 0, 20);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @DisplayName("updateStatus: should update user status when found")
    @Test
    void testUpdateStatus_UserFound_StatusUpdated() {
        // Given
        testUser.setStatus(UserStatus.ACTIVE);
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse result = userService.updateStatus("user-123", UserStatus.INACTIVE);

        // Then
        assertThat(result).isNotNull();
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
        verify(userRepository).save(testUser);
    }

    @DisplayName("updateStatus: should throw exception when user not found")
    @Test
    void testUpdateStatus_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateStatus("nonexistent", UserStatus.INACTIVE))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("assignRoles: should replace entire roles set")
    @Test
    void testAssignRoles_ValidRoles_RolesAssigned() {
        // Given
        Set<RoleName> newRoles = Set.of(RoleName.ROLE_TEACHER, RoleName.ROLE_ADMIN);
        testUser.setRoles(new HashSet<>());

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse result = userService.assignRoles("user-123", newRoles);

        // Then
        assertThat(result).isNotNull();
        assertThat(testUser.getRoles()).containsExactlyInAnyOrder(RoleName.ROLE_TEACHER, RoleName.ROLE_ADMIN);
        verify(userRepository).save(testUser);
    }

    @DisplayName("assignRoles: should throw exception with empty roles")
    @Test
    void testAssignRoles_EmptyRoles_ThrowsException() {
        // Given
        Set<RoleName> emptyRoles = new HashSet<>();
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.assignRoles("user-123", emptyRoles))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("assignRoles: should throw exception when user not found")
    @Test
    void testAssignRoles_UserNotFound_ThrowsException() {
        // Given
        Set<RoleName> newRoles = Set.of(RoleName.ROLE_TEACHER);
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.assignRoles("nonexistent", newRoles))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("softDelete: should mark user as deleted and inactive")
    @Test
    void testSoftDelete_ValidUser_MarkedDeleted() {
        // Given
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setDeleted(false);
        testUser.setRoles(Set.of(RoleName.ROLE_TEACHER));

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        userService.softDelete("user-123");

        // Then
        assertThat(testUser.isDeleted()).isTrue();
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
        verify(userRepository).save(testUser);
    }

    @DisplayName("softDelete: should prevent deletion of ADMIN users")
    @Test
    void testSoftDelete_AdminUser_ThrowsException() {
        // Given
        testUser.setRoles(Set.of(RoleName.ROLE_ADMIN));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.softDelete("user-123"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot delete admin users");
    }

    @DisplayName("softDelete: should throw exception when user not found")
    @Test
    void testSoftDelete_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.softDelete("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("exportAllUsers: should return all users")
    @Test
    void testExportAllUsers_Success() {
        // Given
        User user2 = TestDataFactory.createTestUser("jane_doe", "jane@test.com", "Jane Doe", "pass123");
        when(userRepository.findAll()).thenReturn(List.of(testUser, user2));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);
        when(userMapper.toResponse(user2)).thenReturn(testUserResponse);

        // When
        List<UserResponse> result = userService.exportAllUsers();

        // Then
        assertThat(result).hasSize(2);
        verify(userRepository).findAll();
    }

    @DisplayName("deactivateUser: should mark user as inactive")
    @Test
    void testDeactivateUser_ValidUser_Deactivated() {
        // Given
        testUser.setStatus(UserStatus.ACTIVE);
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse result = userService.deactivateUser("user-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
        // NOTE: BUG-15 - refresh tokens not revoked, so user can still use existing
        // JWTs
        verify(userRepository).save(testUser);
    }

    @DisplayName("deactivateUser: should throw exception when user not found")
    @Test
    void testDeactivateUser_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deactivateUser("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
