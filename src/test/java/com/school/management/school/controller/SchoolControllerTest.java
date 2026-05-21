package com.school.management.school.controller;

import com.school.management.common.test.BaseControllerTest;
import com.school.management.common.test.TestDataFactory;
import com.school.management.school.application.dto.response.SchoolResponse;
import com.school.management.school.application.service.SchoolService;
import com.school.management.school.domain.School;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SchoolController.
 * Tests school CRUD endpoints and branch management.
 */
@DisplayName("SchoolController Tests")
class SchoolControllerTest extends BaseControllerTest {

    @MockBean
    private SchoolService schoolService;

    private School testSchool;
    private SchoolResponse testSchoolResponse;

    @BeforeEach
    void setUp() {
        TestDataFactory.reset();
        testSchool = TestDataFactory.createTestSchool();
        testSchool.setId("school-123");

        testSchoolResponse = new SchoolResponse();
        testSchoolResponse.setId("school-123");
        testSchoolResponse.setSchoolName("Test School");
        testSchoolResponse.setSchoolCode("TST-001");
        testSchoolResponse.setEmailAddress("school@test.com");
        testSchoolResponse.setPhoneNumber("123-456-7890");
    }

    @DisplayName("POST /api/v1/schools: should create new school")
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateSchool_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        when(schoolService.createSchool(any(School.class))).thenReturn(testSchoolResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(testSchool)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.schoolCode").value("TST-001"))
                .andExpect(jsonPath("$.schoolName").value("Test School"));
    }

    @DisplayName("GET /api/v1/schools/{id}: should return school by id")
    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    void testGetSchoolById_SchoolFound_ReturnsSchool() throws Exception {
        // Given
        when(schoolService.getSchoolById("school-123")).thenReturn(testSchoolResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/schools/school-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("school-123"))
                .andExpect(jsonPath("$.schoolCode").value("TST-001"));
    }

    @DisplayName("GET /api/v1/schools/{id}: should return 404 when school not found")
    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    void testGetSchoolById_SchoolNotFound_Returns404() throws Exception {
        // Given
        when(schoolService.getSchoolById("nonexistent")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/v1/schools/nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("PUT /api/v1/schools/{id}: should update school details")
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateSchool_ValidData_Returns200() throws Exception {
        // Given
        testSchoolResponse.setSchoolName("Updated School Name");
        when(schoolService.updateSchool(eq("school-123"), any(School.class)))
                .thenReturn(testSchoolResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/schools/school-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(testSchool)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schoolName").value("Updated School Name"));
    }

    @DisplayName("PATCH /api/v1/schools/{id}/deactivate: should deactivate school")
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeactivateSchool_ValidSchool_Returns200() throws Exception {
        // Given
        when(schoolService.deactivateSchool("school-123")).thenReturn(testSchoolResponse);

        // When & Then
        mockMvc.perform(patch("/api/v1/schools/school-123/deactivate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("GET /api/v1/schools/{id}/branches: should return all branches")
    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    void testGetBranches_SchoolFound_ReturnsBranches() throws Exception {
        // Given
        when(schoolService.getBranches("school-123"))
                .thenReturn(java.util.List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/schools/school-123/branches")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @DisplayName("POST /api/v1/schools/{id}/academic-years: should create academic year")
    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    void testCreateAcademicYear_ValidData_ReturnsCreated() throws Exception {
        // Given
        // When & Then
        mockMvc.perform(post("/api/v1/schools/school-123/academic-years")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(TestDataFactory.createTestAcademicYear("school-123"))))
                .andExpect(status().isCreated());
    }

    @DisplayName("GET /api/v1/schools/{id}/academic-years/current: should return current academic year")
    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    void testGetCurrentAcademicYear_Found_ReturnsYear() throws Exception {
        // Given
        // When & Then
        mockMvc.perform(get("/api/v1/schools/school-123/academic-years/current")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("POST /api/v1/schools: should validate required fields")
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateSchool_MissingSchoolCode_ReturnsBadRequest() throws Exception {
        // Given
        School invalidSchool = new School();
        // Missing school code

        // When & Then
        mockMvc.perform(post("/api/v1/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(invalidSchool)))
                .andExpect(status().isBadRequest());
    }
}
