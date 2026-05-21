package com.school.management.school.controller;

import com.school.management.common.response.ApiResponse;
import com.school.management.common.response.PagedResponse;
import com.school.management.school.application.dto.request.CreateSchoolRequest;
import com.school.management.school.application.dto.response.SchoolResponse;
import com.school.management.school.application.service.SchoolService;
import com.school.management.school.domain.AcademicYear;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
@Tag(name = "Schools", description = "School and tenant management APIs")
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping
    @Operation(summary = "Create school")
    public ResponseEntity<ApiResponse<SchoolResponse>> create(@Valid @RequestBody CreateSchoolRequest request) {
        return ResponseEntity.ok(ApiResponse.success(schoolService.create(request), "School created"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get school by ID")
    public ResponseEntity<ApiResponse<SchoolResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(schoolService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "List schools")
    public ResponseEntity<ApiResponse<PagedResponse<SchoolResponse>>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(ApiResponse.success(schoolService.list(page, size)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate school")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String id) {
        schoolService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.successMessage("School deactivated"));
    }

    /**
     * school-008: POST /api/v1/schools/{id}/academic-years — no date range
     * validation
     */
    @PostMapping("/{id}/academic-years")
    @Operation(summary = "Create academic year (no date validation)")
    public ResponseEntity<ApiResponse<AcademicYear>> createAcademicYear(
            @PathVariable String id,
            @RequestParam String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(
                schoolService.createAcademicYear(id, name, startDate, endDate),
                "Academic year created"));
    }

    /**
     * school-011: PUT /api/v1/schools/{id} — null fields overwrite existing data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update school (null fields overwrite existing data)")
    public ResponseEntity<ApiResponse<SchoolResponse>> update(
            @PathVariable String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {
        return ResponseEntity.ok(ApiResponse.success(schoolService.update(id, name, email, phone), "School updated"));
    }

    /** school-013: GET /api/v1/schools/{id}/users — no authorization check */
    @GetMapping("/{id}/users")
    @Operation(summary = "List users of a school (no role check)")
    public ResponseEntity<ApiResponse<List<String>>> getUsersBySchool(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(schoolService.getUsersBySchool(id)));
    }
}
