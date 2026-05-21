package com.school.management.student.application.service;

import com.school.management.common.exception.BusinessException;
import com.school.management.common.exception.ResourceNotFoundException;
import com.school.management.common.test.BaseServiceTest;
import com.school.management.common.test.TestDataFactory;
import com.school.management.student.application.dto.response.StudentResponse;
import com.school.management.student.application.mapper.StudentMapper;
import com.school.management.student.domain.Student;
import com.school.management.student.domain.StudentStatus;
import com.school.management.student.infrastructure.StudentRepository;
import com.school.management.student.infrastructure.ParentGuardianRepository;
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
 * Unit tests for StudentService.
 * Tests student lifecycle operations: admission, search, promotion, transfer,
 * status updates.
 */
@DisplayName("StudentService Tests")
class StudentServiceTest extends BaseServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ParentGuardianRepository parentGuardianRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private StudentResponse testStudentResponse;

    @BeforeEach
    void setUp() {
        TestDataFactory.reset();
        testStudent = TestDataFactory.createTestStudent("TST-001", "John", "Doe");
        testStudent.setId("student-123");
        testStudent.setStatus(StudentStatus.ACTIVE);

        testStudentResponse = new StudentResponse();
        testStudentResponse.setId("student-123");
        testStudentResponse.setAdmissionNumber("TST-001");
        testStudentResponse.setFirstName("John");
        testStudentResponse.setLastName("Doe");
        testStudentResponse.setStatus(StudentStatus.ACTIVE);
    }

    @DisplayName("admitStudent: should create new student with ACTIVE status")
    @Test
    void testAdmitStudent_ValidRequest_Success() {
        // Given
        Student newStudent = TestDataFactory.createTestStudent("TST-002", "Jane", "Smith");
        when(studentRepository.existsByAdmissionNumber(newStudent.getAdmissionNumber()))
                .thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(newStudent);
        when(studentMapper.toResponse(newStudent)).thenReturn(testStudentResponse);

        // When
        StudentResponse result = studentService.admitStudent(newStudent);

        // Then
        assertThat(result).isNotNull();
        assertThat(newStudent.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        verify(studentRepository).save(any(Student.class));
    }

    @DisplayName("admitStudent: should throw exception for duplicate admission number")
    @Test
    void testAdmitStudent_DuplicateAdmissionNumber_ThrowsException() {
        // Given
        when(studentRepository.existsByAdmissionNumber(testStudent.getAdmissionNumber()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> studentService.admitStudent(testStudent))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Admission number already exists");
    }

    @DisplayName("getStudentById: should return student when found")
    @Test
    void testGetStudentById_StudentFound_ReturnsStudent() {
        // Given
        when(studentRepository.findById("student-123")).thenReturn(Optional.of(testStudent));
        when(studentMapper.toResponse(testStudent)).thenReturn(testStudentResponse);

        // When
        StudentResponse result = studentService.getStudentById("student-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAdmissionNumber()).isEqualTo("TST-001");
        verify(studentRepository).findById("student-123");
    }

    @DisplayName("getStudentById: should throw exception when student not found")
    @Test
    void testGetStudentById_StudentNotFound_ThrowsException() {
        // Given
        when(studentRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.getStudentById("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("promoteStudent: should update grade when not transferred")
    @Test
    void testPromoteStudent_ActiveStudent_Success() {
        // Given
        testStudent.setStatus(StudentStatus.ACTIVE);
        testStudent.setCurrentGrade("10A");
        String newGrade = "11A";

        when(studentRepository.findById("student-123")).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(testStudent)).thenReturn(testStudent);
        when(studentMapper.toResponse(testStudent)).thenReturn(testStudentResponse);

        // When
        StudentResponse result = studentService.promoteStudent("student-123", newGrade);

        // Then
        assertThat(result).isNotNull();
        assertThat(testStudent.getCurrentGrade()).isEqualTo(newGrade);
        verify(studentRepository).save(testStudent);
    }

    @DisplayName("promoteStudent: should throw exception for transferred student")
    @Test
    void testPromoteStudent_TransferredStudent_ThrowsException() {
        // Given
        testStudent.setStatus(StudentStatus.TRANSFERRED);
        when(studentRepository.findById("student-123")).thenReturn(Optional.of(testStudent));

        // When & Then
        assertThatThrownBy(() -> studentService.promoteStudent("student-123", "11A"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Transferred");
    }

    @DisplayName("transferStudent: should change school and set TRANSFERRED status")
    @Test
    void testTransferStudent_ValidSchool_Success() {
        // Given
        String newSchoolId = "school-456";
        when(studentRepository.findById("student-123")).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(testStudent)).thenReturn(testStudent);
        when(studentMapper.toResponse(testStudent)).thenReturn(testStudentResponse);

        // When
        StudentResponse result = studentService.transferStudent("student-123", newSchoolId);

        // Then
        assertThat(result).isNotNull();
        assertThat(testStudent.getSchoolId()).isEqualTo(newSchoolId);
        assertThat(testStudent.getStatus()).isEqualTo(StudentStatus.TRANSFERRED);
        verify(studentRepository).save(testStudent);
    }

    @DisplayName("updateStudentStatus: should update status successfully")
    @Test
    void testUpdateStudentStatus_ValidStatus_Success() {
        // Given
        when(studentRepository.findById("student-123")).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(testStudent)).thenReturn(testStudent);
        when(studentMapper.toResponse(testStudent)).thenReturn(testStudentResponse);

        // When
        StudentResponse result = studentService.updateStudentStatus("student-123", StudentStatus.SUSPENDED);

        // Then
        assertThat(result).isNotNull();
        assertThat(testStudent.getStatus()).isEqualTo(StudentStatus.SUSPENDED);
        verify(studentRepository).save(testStudent);
    }

    @DisplayName("deactivateStudent: should mark student as inactive")
    @Test
    void testDeactivateStudent_ValidStudent_Success() {
        // Given
        when(studentRepository.findById("student-123")).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(testStudent)).thenReturn(testStudent);
        when(studentMapper.toResponse(testStudent)).thenReturn(testStudentResponse);

        // When
        StudentResponse result = studentService.deactivateStudent("student-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(testStudent.getStatus()).isEqualTo(StudentStatus.INACTIVE);
        verify(studentRepository).save(testStudent);
    }

    @DisplayName("getStudentsByGrade: should return students for specific grade")
    @Test
    void testGetStudentsByGrade_ValidGrade_ReturnsStudents() {
        // Given
        testStudent.setCurrentGrade("10A");
        when(studentRepository.findByCurrentGradeAndStatusAndDeletedFalse("10A", StudentStatus.ACTIVE))
                .thenReturn(java.util.List.of(testStudent));
        when(studentMapper.toResponse(testStudent)).thenReturn(testStudentResponse);

        // When
        var result = studentService.getStudentsByGrade("10A", StudentStatus.ACTIVE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAdmissionNumber()).isEqualTo("TST-001");
    }

    @DisplayName("validateStudentAgeRequirements: should verify minimum age for admission")
    @Test
    void testValidateStudentAgeRequirements_TooYoung_ThrowsException() {
        // Given
        Student youngStudent = TestDataFactory.createTestStudent("TST-003", "Baby", "Student");
        youngStudent.setDateOfBirth(LocalDate.now().minusYears(3)); // Only 3 years old

        // When & Then
        assertThatThrownBy(() -> studentService.validateStudentAgeRequirements(youngStudent))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("minimum age");
    }

    @DisplayName("validateStudentAgeRequirements: should accept valid age")
    @Test
    void testValidateStudentAgeRequirements_ValidAge_Success() {
        // Given
        testStudent.setDateOfBirth(LocalDate.now().minusYears(15)); // 15 years old

        // When & Then
        assertThatDoesNotThrow(() -> studentService.validateStudentAgeRequirements(testStudent));
    }
}
