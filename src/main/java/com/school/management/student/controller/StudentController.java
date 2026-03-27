package com.school.management.student.controller;

import com.school.management.common.response.ApiResponse;
import com.school.management.common.response.PagedResponse;
import com.school.management.student.application.dto.request.CreateStudentRequest;
import com.school.management.student.application.dto.request.PromoteStudentRequest;
import com.school.management.student.application.dto.response.StudentResponse;
import com.school.management.student.application.service.StudentService;
import com.school.management.student.domain.StudentStatus;
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
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student lifecycle management APIs")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Admit a new student")
    public ResponseEntity<ApiResponse<StudentResponse>> admit(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.admit(request), "Student admitted"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN','TEACHER')")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN','TEACHER')")
    @Operation(summary = "Search students")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponse>>> search(
            @RequestParam String schoolId,
            @RequestParam(required = false) StudentStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        return ResponseEntity.ok(ApiResponse.success(studentService.search(schoolId, status, search, page, size)));
    }

    @PatchMapping("/{id}/promote")
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Promote student to next grade")
    public ResponseEntity<ApiResponse<StudentResponse>> promote(@PathVariable String id,
                                                                @Valid @RequestBody PromoteStudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.promote(id, request), "Student promoted"));
    }

    @PatchMapping("/{id}/transfer")
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Mark student as transferred")
    public ResponseEntity<ApiResponse<StudentResponse>> transfer(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.transfer(id), "Student transferred"));
    }

    @PatchMapping("/{id}/dropout")
    @PreAuthorize("hasAnyAuthority('ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Mark student as dropped out")
    public ResponseEntity<ApiResponse<StudentResponse>> dropOut(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.dropOut(id), "Student marked as dropped out"));
    }
}
