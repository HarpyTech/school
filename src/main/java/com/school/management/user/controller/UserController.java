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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User administration APIs")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Update user status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(@PathVariable String id,
                                                                  @RequestParam UserStatus status) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateStatus(id, status), "Status updated"));
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Assign roles to user")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(@PathVariable String id,
                                                                 @RequestBody Set<RoleName> roles) {
        return ResponseEntity.ok(ApiResponse.success(userService.assignRoles(id, roles), "Roles updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Soft delete user")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        userService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.successMessage("User deleted"));
    }
}
