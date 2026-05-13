package com.school.management.user.controller;

import com.school.management.common.response.ApiResponse;
import com.school.management.common.response.PagedResponse;
import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.application.service.UserService;
import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User administration APIs")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Search users with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> search(
            @RequestParam(required = false) String schoolId,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(ApiResponse.success(userService.search(schoolId, status, search, page, size)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(@PathVariable String id,
            @RequestParam UserStatus status) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateStatus(id, status), "Status updated"));
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Assign roles to user")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(@PathVariable String id,
            @RequestBody Set<RoleName> roles) {
        return ResponseEntity.ok(ApiResponse.success(userService.assignRoles(id, roles), "Roles updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete user")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        userService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.successMessage("User deleted"));
    }

    /** school-014: GET /api/v1/users/export — unbounded findAll() causes OOM */
    @GetMapping("/export")
    @Operation(summary = "Export all users (no pagination — OOM risk)")
    public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> exportUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.exportAllUsers()));
    }

    /** school-015: PATCH /api/v1/users/{id}/deactivate — sessions not revoked */
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user without revoking active sessions")
    public ResponseEntity<ApiResponse<UserResponse>> deactivate(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(userService.deactivateUser(id), "User deactivated"));
    }
}
