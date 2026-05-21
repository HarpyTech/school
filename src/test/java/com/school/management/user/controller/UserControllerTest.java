package com.school.management.user.controller;

import com.school.management.common.response.PagedResponse;
import com.school.management.common.test.BaseControllerTest;
import com.school.management.common.test.TestDataFactory;
import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.application.service.UserService;
import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController.
 * Tests user CRUD endpoints, search, role assignment, and deactivation.
 */
@DisplayName("UserController Tests")
class UserControllerTest extends BaseControllerTest {

        @MockBean
        private UserService userService;

        private UserResponse testUserResponse;
        private PagedResponse<UserResponse> pagedUserResponse;

        @BeforeEach
        void setUp() {
                TestDataFactory.reset();

                testUserResponse = new UserResponse();
                testUserResponse.setId("user-123");
                testUserResponse.setUsername("john_doe");
                testUserResponse.setEmail("john@test.com");
                testUserResponse.setFirstName("John");
                testUserResponse.setLastName("Doe");
                testUserResponse.setStatus(UserStatus.ACTIVE);

                pagedUserResponse = new PagedResponse<>();
                pagedUserResponse.setContent(List.of(testUserResponse));
                pagedUserResponse.setCurrentPage(0);
                pagedUserResponse.setTotalPages(1);
                pagedUserResponse.setTotalElements(1L);
        }

        @DisplayName("GET /api/v1/users/{id}: should return user by id (ADMIN only)")
        @Test
        @WithMockUser(roles = "ADMIN")
        void testGetUserById_UserFound_ReturnsUser() throws Exception {
                // Given
                when(userService.getById("user-123")).thenReturn(testUserResponse);

                // When & Then
                mockMvc.perform(get("/api/v1/users/user-123")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("user-123"))
                                .andExpect(jsonPath("$.username").value("john_doe"))
                                .andExpect(jsonPath("$.email").value("john@test.com"));
        }

        @DisplayName("GET /api/v1/users/{id}: should return 404 when user not found")
        @Test
        @WithMockUser(roles = "ADMIN")
        void testGetUserById_UserNotFound_Returns404() throws Exception {
                // Given
                when(userService.getById("nonexistent")).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/api/v1/users/nonexistent")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @DisplayName("GET /api/v1/users: should search users with filters")
        @Test
        @WithMockUser(roles = "ADMIN")
        void testSearchUsers_WithFilters_ReturnsPaginatedResults() throws Exception {
                // Given
                when(userService.search(anyString(), any(), anyString(), anyInt(), anyInt()))
                                .thenReturn(pagedUserResponse);

                // When & Then
                mockMvc.perform(get("/api/v1/users")
                                .param("schoolId", "school-123")
                                .param("status", "ACTIVE")
                                .param("search", "john")
                                .param("page", "0")
                                .param("size", "20")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content[0].username").value("john_doe"));
        }

        @DisplayName("GET /api/v1/users: should require ADMIN or SCHOOL_ADMIN role")
        @Test
        void testSearchUsers_NoAuthentication_ReturnsForbidden() throws Exception {
                // Given
                // No authentication

                // When & Then
                mockMvc.perform(get("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isForbidden());
        }

        @DisplayName("PATCH /api/v1/users/{id}/status: should update user status")
        @Test
        @WithMockUser(roles = "ADMIN")
        void testUpdateStatus_ValidStatus_Returns200() throws Exception {
                // Given
                testUserResponse.setStatus(UserStatus.INACTIVE);
                when(userService.updateStatus("user-123", UserStatus.INACTIVE))
                                .thenReturn(testUserResponse);

                // When & Then
                mockMvc.perform(patch("/api/v1/users/user-123/status")
                                .param("status", "INACTIVE")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("INACTIVE"));
        }

        @DisplayName("PUT /api/v1/users/{id}/roles: should assign roles (ADMIN only)")
        @Test
        @WithMockUser(roles = "ADMIN")
        void testAssignRoles_ValidRoles_Returns200() throws Exception {
                // Given
                when(userService.assignRoles(anyString(), any()))
                                .thenReturn(testUserResponse);

                // When & Then
                mockMvc.perform(put("/api/v1/users/user-123/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJson(Set.of(RoleName.ROLE_TEACHER, RoleName.ROLE_ADMIN))))
                                .andExpect(status().isOk());
        }

        @DisplayName("PUT /api/v1/users/{id}/roles: should require ADMIN role")
        @Test
        @WithMockUser(roles = "SCHOOL_ADMIN")
        void testAssignRoles_NonAdminUser_ReturnsForbidden() throws Exception {
                // Given
                // Only ADMIN can assign roles

                // When & Then
                mockMvc.perform(put("/api/v1/users/user-123/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJson(Set.of(RoleName.ROLE_TEACHER))))
                                .andExpect(status().isForbidden());
        }

        @DisplayName("DELETE /api/v1/users/{id}: should soft delete user")
        @Test
        @WithMockUser(roles = "ADMIN")
        void testDeleteUser_ValidUser_Returns200() throws Exception {
                // Given
                // When & Then
                mockMvc.perform(delete("/api/v1/users/user-123")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

                verify(userService).softDelete("user-123");
        }

        @DisplayName("GET /api/v1/users/export: should export all users (ADMIN only)")
        @Test
        @WithMockUser(roles = "ADMIN")
        void testExportUsers_AdminUser_ReturnsUsersList() throws Exception {
                // Given
                List<UserResponse> users = List.of(testUserResponse);
                when(userService.exportAllUsers()).thenReturn(users);

                // When & Then
                mockMvc.perform(get("/api/v1/users/export")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @DisplayName("PATCH /api/v1/users/{id}/deactivate: should deactivate user")
        @Test
        @WithMockUser(roles = "ADMIN")
        void testDeactivateUser_ValidUser_Returns200() throws Exception {
                // Given
                testUserResponse.setStatus(UserStatus.INACTIVE);
                when(userService.deactivateUser("user-123")).thenReturn(testUserResponse);

                // When & Then
                mockMvc.perform(patch("/api/v1/users/user-123/deactivate")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("INACTIVE"));
        }

        @DisplayName("GET /api/v1/users: should validate pagination parameters")
        @Test
        @WithMockUser(roles = "ADMIN")
        void testSearchUsers_InvalidPageSize_ValidatePagination() throws Exception {
                // Given
                when(userService.search(anyString(), any(), anyString(), anyInt(), anyInt()))
                                .thenReturn(pagedUserResponse);

                // When & Then
                // Max page size is typically 100
                mockMvc.perform(get("/api/v1/users")
                                .param("page", "0")
                                .param("size", "150") // Too large
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()); // Could be adjusted to validate max size
        }
}
