package com.school.management.student.controller;

import com.school.management.common.response.PagedResponse;
import com.school.management.common.test.BaseControllerTest;
import com.school.management.common.test.TestDataFactory;
import com.school.management.student.application.dto.response.StudentResponse;
import com.school.management.student.application.service.StudentService;
import com.school.management.student.domain.Student;
import com.school.management.student.domain.StudentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StudentController.
 * Tests student CRUD endpoints, admission, promotion, transfer, status updates.
 */
@DisplayName("StudentController Tests")
class StudentControllerTest extends BaseControllerTest {

        @MockBean
        private StudentService studentService;

        private Student testStudent;
        private StudentResponse testStudentResponse;
        private PagedResponse<StudentResponse> pagedStudentResponse;

        @BeforeEach
        void setUp() {
                TestDataFactory.reset();
                testStudent = TestDataFactory.createTestStudent("TST-001", "John", "Doe");
                testStudent.setId("student-123");
                testStudent.setStatus(StudentStatus.ACTIVE);
                testStudent.setCurrentGrade("10A");

                testStudentResponse = new StudentResponse();
                testStudentResponse.setId("student-123");
                testStudentResponse.setAdmissionNumber("TST-001");
                testStudentResponse.setFirstName("John");
                testStudentResponse.setLastName("Doe");
                testStudentResponse.setCurrentGrade("10A");
                testStudentResponse.setStatus(StudentStatus.ACTIVE);

                pagedStudentResponse = new PagedResponse<>();
                pagedStudentResponse.setContent(List.of(testStudentResponse));
                pagedStudentResponse.setCurrentPage(0);
                pagedStudentResponse.setTotalPages(1);
                pagedStudentResponse.setTotalElements(1L);
        }

        @DisplayName("POST /api/v1/students: should admit new student")
        @Test
        @WithMockUser(roles = "SCHOOL_ADMIN")
        void testAdmitStudent_ValidRequest_ReturnsCreated() throws Exception {
                // Given
                when(studentService.admitStudent(any(Student.class))).thenReturn(testStudentResponse);

                // When & Then
                mockMvc.perform(post("/api/v1/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJson(testStudent)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.admissionNumber").value("TST-001"))
                                .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @DisplayName("GET /api/v1/students/{id}: should return student by id")
        @Test
        @WithMockUser(roles = "TEACHER")
        void testGetStudentById_StudentFound_ReturnsStudent() throws Exception {
                // Given
                when(studentService.getStudentById("student-123")).thenReturn(testStudentResponse);

                // When & Then
                mockMvc.perform(get("/api/v1/students/student-123")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("student-123"))
                                .andExpect(jsonPath("$.admissionNumber").value("TST-001"))
                                .andExpect(jsonPath("$.currentGrade").value("10A"));
        }

        @DisplayName("GET /api/v1/students/{id}: should return 404 when student not found")
        @Test
        @WithMockUser(roles = "TEACHER")
        void testGetStudentById_StudentNotFound_Returns404() throws Exception {
                // Given
                when(studentService.getStudentById("nonexistent")).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/api/v1/students/nonexistent")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @DisplayName("GET /api/v1/students: should search students with pagination")
        @Test
        @WithMockUser(roles = "SCHOOL_ADMIN")
        void testSearchStudents_WithFilters_ReturnsPaginatedResults() throws Exception {
                // Given
                when(studentService.searchStudents(any(), any(), any(), any()))
                                .thenReturn(pagedStudentResponse);

                // When & Then
                mockMvc.perform(get("/api/v1/students")
                                .param("schoolId", "school-123")
                                .param("status", "ACTIVE")
                                .param("currentGrade", "10A")
                                .param("page", "0")
                                .param("size", "20")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content[0].admissionNumber").value("TST-001"));
        }

        @DisplayName("PUT /api/v1/students/{id}/promote: should promote student to next grade")
        @Test
        @WithMockUser(roles = "SCHOOL_ADMIN")
        void testPromoteStudent_ValidGrade_Returns200() throws Exception {
                // Given
                testStudentResponse.setCurrentGrade("11A");
                when(studentService.promoteStudent("student-123", "11A"))
                                .thenReturn(testStudentResponse);

                // When & Then
                mockMvc.perform(put("/api/v1/students/student-123/promote")
                                .param("newGrade", "11A")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.currentGrade").value("11A"));
        }

        @DisplayName("PUT /api/v1/students/{id}/promote: should prevent promotion of transferred student")
        @Test
        @WithMockUser(roles = "SCHOOL_ADMIN")
        void testPromoteStudent_TransferredStudent_ReturnsBadRequest() throws Exception {
                // Given
                when(studentService.promoteStudent("student-123", "11A"))
                                .thenThrow(new IllegalArgumentException("Cannot promote transferred student"));

                // When & Then
                mockMvc.perform(put("/api/v1/students/student-123/promote")
                                .param("newGrade", "11A")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().is4xxClientError());
        }

        @DisplayName("PUT /api/v1/students/{id}/transfer: should transfer student to new school")
        @Test
        @WithMockUser(roles = "SCHOOL_ADMIN")
        void testTransferStudent_ValidSchool_Returns200() throws Exception {
                // Given
                testStudentResponse.setStatus(StudentStatus.TRANSFERRED);
                when(studentService.transferStudent("student-123", "school-456"))
                                .thenReturn(testStudentResponse);

                // When & Then
                mockMvc.perform(put("/api/v1/students/student-123/transfer")
                                .param("newSchoolId", "school-456")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("TRANSFERRED"));
        }

        @DisplayName("PATCH /api/v1/students/{id}/status: should update student status")
        @Test
        @WithMockUser(roles = "SCHOOL_ADMIN")
        void testUpdateStudentStatus_ValidStatus_Returns200() throws Exception {
                // Given
                testStudentResponse.setStatus(StudentStatus.SUSPENDED);
                when(studentService.updateStudentStatus("student-123", StudentStatus.SUSPENDED))
                                .thenReturn(testStudentResponse);

                // When & Then
                mockMvc.perform(patch("/api/v1/students/student-123/status")
                                .param("status", "SUSPENDED")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("SUSPENDED"));
        }

        @DisplayName("PATCH /api/v1/students/{id}/deactivate: should deactivate student")
        @Test
        @WithMockUser(roles = "SCHOOL_ADMIN")
        void testDeactivateStudent_ValidStudent_Returns200() throws Exception {
                // Given
                testStudentResponse.setStatus(StudentStatus.INACTIVE);
                when(studentService.deactivateStudent("student-123"))
                                .thenReturn(testStudentResponse);

                // When & Then
                mockMvc.perform(patch("/api/v1/students/student-123/deactivate")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("INACTIVE"));
        }

        @DisplayName("GET /api/v1/students/grade/{grade}: should return students by grade")
        @Test
        @WithMockUser(roles = "TEACHER")
        void testGetStudentsByGrade_ValidGrade_ReturnsStudents() throws Exception {
                // Given
                when(studentService.getStudentsByGrade("10A", StudentStatus.ACTIVE))
                                .thenReturn(List.of(testStudentResponse));

                // When & Then
                mockMvc.perform(get("/api/v1/students/grade/10A")
                                .param("status", "ACTIVE")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].currentGrade").value("10A"));
        }

        @DisplayName("POST /api/v1/students: should validate required fields")
        @Test
        @WithMockUser(roles = "SCHOOL_ADMIN")
        void testAdmitStudent_MissingAdmissionNumber_ReturnsBadRequest() throws Exception {
                // Given
                Student invalidStudent = new Student();
                // Missing admission number

                // When & Then
                mockMvc.perform(post("/api/v1/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJson(invalidStudent)))
                                .andExpect(status().isBadRequest());
        }
}
