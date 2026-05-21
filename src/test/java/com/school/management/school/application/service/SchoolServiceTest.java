package com.school.management.school.application.service;

import com.school.management.common.exception.BusinessException;
import com.school.management.common.exception.ResourceNotFoundException;
import com.school.management.common.test.BaseServiceTest;
import com.school.management.common.test.TestDataFactory;
import com.school.management.school.application.dto.response.SchoolResponse;
import com.school.management.school.application.mapper.SchoolMapper;
import com.school.management.school.domain.School;
import com.school.management.school.domain.Branch;
import com.school.management.school.domain.AcademicYear;
import com.school.management.school.infrastructure.SchoolRepository;
import com.school.management.school.infrastructure.BranchRepository;
import com.school.management.school.infrastructure.AcademicYearRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SchoolService.
 * Tests school CRUD operations, branch management, and academic year
 * configuration.
 */
@DisplayName("SchoolService Tests")
class SchoolServiceTest extends BaseServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private SchoolMapper schoolMapper;

    @InjectMocks
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
    }

    @DisplayName("createSchool: should create new school successfully")
    @Test
    void testCreateSchool_ValidSchool_Success() {
        // Given
        when(schoolRepository.existsBySchoolCode(testSchool.getSchoolCode()))
                .thenReturn(false);
        when(schoolRepository.save(any(School.class))).thenReturn(testSchool);
        when(schoolMapper.toResponse(testSchool)).thenReturn(testSchoolResponse);

        // When
        SchoolResponse result = schoolService.createSchool(testSchool);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSchoolCode()).isEqualTo("TST-001");
        verify(schoolRepository).save(any(School.class));
    }

    @DisplayName("createSchool: should throw exception for duplicate school code")
    @Test
    void testCreateSchool_DuplicateCode_ThrowsException() {
        // Given
        when(schoolRepository.existsBySchoolCode(testSchool.getSchoolCode()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> schoolService.createSchool(testSchool))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("School code already exists");
    }

    @DisplayName("getSchoolById: should return school when found")
    @Test
    void testGetSchoolById_SchoolFound_ReturnsSchool() {
        // Given
        when(schoolRepository.findById("school-123")).thenReturn(Optional.of(testSchool));
        when(schoolMapper.toResponse(testSchool)).thenReturn(testSchoolResponse);

        // When
        SchoolResponse result = schoolService.getSchoolById("school-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSchoolCode()).isEqualTo("TST-001");
        verify(schoolRepository).findById("school-123");
    }

    @DisplayName("getSchoolById: should throw exception when school not found")
    @Test
    void testGetSchoolById_SchoolNotFound_ThrowsException() {
        // Given
        when(schoolRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> schoolService.getSchoolById("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("updateSchool: should update school details")
    @Test
    void testUpdateSchool_ValidData_Success() {
        // Given
        testSchool.setSchoolName("Updated School Name");
        when(schoolRepository.findById("school-123")).thenReturn(Optional.of(testSchool));
        when(schoolRepository.save(testSchool)).thenReturn(testSchool);
        when(schoolMapper.toResponse(testSchool)).thenReturn(testSchoolResponse);

        // When
        SchoolResponse result = schoolService.updateSchool("school-123", testSchool);

        // Then
        assertThat(result).isNotNull();
        assertThat(testSchool.getSchoolName()).isEqualTo("Updated School Name");
        verify(schoolRepository).save(testSchool);
    }

    @DisplayName("deactivateSchool: should mark school as inactive")
    @Test
    void testDeactivateSchool_ValidSchool_Success() {
        // Given
        testSchool.setIsActive(true);
        when(schoolRepository.findById("school-123")).thenReturn(Optional.of(testSchool));
        when(schoolRepository.save(testSchool)).thenReturn(testSchool);
        when(schoolMapper.toResponse(testSchool)).thenReturn(testSchoolResponse);

        // When
        SchoolResponse result = schoolService.deactivateSchool("school-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(testSchool.getIsActive()).isFalse();
        verify(schoolRepository).save(testSchool);
    }

    @DisplayName("addBranch: should create new branch for school")
    @Test
    void testAddBranch_ValidBranch_Success() {
        // Given
        Branch branch = TestDataFactory.createTestBranch("school-123");
        when(schoolRepository.findById("school-123")).thenReturn(Optional.of(testSchool));
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);

        // When
        schoolService.addBranch("school-123", branch);

        // Then
        verify(branchRepository).save(any(Branch.class));
    }

    @DisplayName("addBranch: should throw exception when school not found")
    @Test
    void testAddBranch_SchoolNotFound_ThrowsException() {
        // Given
        Branch branch = TestDataFactory.createTestBranch("school-123");
        when(schoolRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> schoolService.addBranch("nonexistent", branch))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("createAcademicYear: should create new academic year")
    @Test
    void testCreateAcademicYear_ValidData_Success() {
        // Given
        AcademicYear academicYear = TestDataFactory.createTestAcademicYear("school-123");
        when(schoolRepository.findById("school-123")).thenReturn(Optional.of(testSchool));
        when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(academicYear);

        // When
        schoolService.createAcademicYear("school-123", academicYear);

        // Then
        verify(academicYearRepository).save(any(AcademicYear.class));
    }

    @DisplayName("getCurrentAcademicYear: should return current academic year")
    @Test
    void testGetCurrentAcademicYear_Found_ReturnsYear() {
        // Given
        AcademicYear academicYear = TestDataFactory.createTestAcademicYear("school-123");
        when(academicYearRepository.findBySchoolIdAndIsCurrentTrue("school-123"))
                .thenReturn(Optional.of(academicYear));

        // When
        AcademicYear result = schoolService.getCurrentAcademicYear("school-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIsCurrent()).isTrue();
    }

    @DisplayName("getCurrentAcademicYear: should throw exception when not found")
    @Test
    void testGetCurrentAcademicYear_NotFound_ThrowsException() {
        // Given
        when(academicYearRepository.findBySchoolIdAndIsCurrentTrue("school-123"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> schoolService.getCurrentAcademicYear("school-123"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("getBranches: should return all branches for school")
    @Test
    void testGetBranches_SchoolFound_ReturnsBranches() {
        // Given
        Branch branch1 = TestDataFactory.createTestBranch("school-123");
        Branch branch2 = TestDataFactory.createTestBranch("school-123");
        branch2.setBranchName("Branch 2");

        when(branchRepository.findBySchoolId("school-123"))
                .thenReturn(java.util.List.of(branch1, branch2));

        // When
        var result = schoolService.getBranches("school-123");

        // Then
        assertThat(result).hasSize(2);
    }
}
