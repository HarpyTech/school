package com.school.management.school.controller;

import com.school.management.common.response.ApiResponse;
import com.school.management.common.response.PagedResponse;
import com.school.management.school.application.dto.request.CreateSchoolRequest;
import com.school.management.school.application.dto.response.SchoolResponse;
import com.school.management.school.application.service.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
@Tag(name = "Schools", description = "School and tenant management APIs")
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create school")
    public ResponseEntity<ApiResponse<SchoolResponse>> create(@Valid @RequestBody CreateSchoolRequest request) {
        return ResponseEntity.ok(ApiResponse.success(schoolService.create(request), "School created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Get school by ID")
    public ResponseEntity<ApiResponse<SchoolResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(schoolService.getById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "List schools")
    public ResponseEntity<ApiResponse<PagedResponse<SchoolResponse>>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(ApiResponse.success(schoolService.list(page, size)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Deactivate school")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String id) {
        schoolService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.successMessage("School deactivated"));
    }
}
