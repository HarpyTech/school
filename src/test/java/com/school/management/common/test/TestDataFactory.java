package com.school.management.common.test;

import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import com.school.management.school.domain.School;
import com.school.management.school.domain.AcademicYear;
import com.school.management.school.domain.Branch;
import com.school.management.student.domain.Student;
import com.school.management.student.domain.StudentStatus;
import com.school.management.student.domain.ParentGuardian;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Factory for creating test entities with sensible defaults.
 * Use these methods in tests to avoid duplicating test data setup.
 */
public class TestDataFactory {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static int userCounter = 1;
    private static int schoolCounter = 1;
    private static int studentCounter = 1;

    // ==================== USER & SECURITY ====================

    /**
     * Create a test User with default values.
     */
    public static User createTestUser() {
        return createTestUser("testuser" + (userCounter++), "test@example.com", "Test User", "password123");
    }

    /**
     * Create a test User with custom username and email.
     */
    public static User createTestUser(String username, String email, String fullName, String rawPassword) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFirstName(fullName.split(" ")[0]);
        user.setLastName(fullName.contains(" ") ? fullName.split(" ", 2)[1] : "User");
        user.setSchoolId(UUID.randomUUID().toString());
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(new HashSet<>());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Create a test Role with default values.
     */
    public static Role createTestRole(RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);
        role.setDescription("Test role: " + roleName.name());
        role.setPermissions(new HashSet<>());
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        return role;
    }

    /**
     * Create a test User with a specific role.
     */
    public static User createTestUserWithRole(RoleName roleName) {
        User user = createTestUser();
        Role role = createTestRole(roleName);
        user.setRoles(Set.of(role));
        return user;
    }

    /**
     * Create an admin User.
     */
    public static User createTestAdminUser() {
        return createTestUserWithRole(RoleName.ROLE_ADMIN);
    }

    /**
     * Create a school admin User.
     */
    public static User createTestSchoolAdminUser() {
        return createTestUserWithRole(RoleName.ROLE_SCHOOL_ADMIN);
    }

    /**
     * Create a teacher User.
     */
    public static User createTestTeacherUser() {
        return createTestUserWithRole(RoleName.ROLE_TEACHER);
    }

    /**
     * Create a parent User.
     */
    public static User createTestParentUser() {
        return createTestUserWithRole(RoleName.ROLE_PARENT);
    }

    // ==================== SCHOOL ====================

    /**
     * Create a test School with default values.
     */
    public static School createTestSchool() {
        School school = new School();
        school.setSchoolName("Test School " + schoolCounter++);
        school.setSchoolCode("TST-" + System.currentTimeMillis());
        school.setAddress("123 Test Street, Test City, TC 12345");
        school.setPhoneNumber("123-456-7890");
        school.setEmailAddress("school@test.com");
        school.setFoundedYear(2020);
        school.setIsActive(true);
        school.setCreatedAt(LocalDateTime.now());
        school.setUpdatedAt(LocalDateTime.now());
        return school;
    }

    /**
     * Create a test Branch.
     */
    public static Branch createTestBranch(String schoolId) {
        Branch branch = new Branch();
        branch.setSchoolId(schoolId);
        branch.setBranchName("Test Branch");
        branch.setBranchCode("BR-001");
        branch.setAddress("456 Branch Street");
        branch.setIsHeadquarters(true);
        branch.setCreatedAt(LocalDateTime.now());
        branch.setUpdatedAt(LocalDateTime.now());
        return branch;
    }

    /**
     * Create a test Academic Year.
     */
    public static AcademicYear createTestAcademicYear(String schoolId) {
        AcademicYear academicYear = new AcademicYear();
        academicYear.setSchoolId(schoolId);
        academicYear.setAcademicYearName("2023-2024");
        academicYear.setStartDate(LocalDate.of(2023, 4, 1));
        academicYear.setEndDate(LocalDate.of(2024, 3, 31));
        academicYear.setIsCurrent(true);
        academicYear.setCreatedAt(LocalDateTime.now());
        academicYear.setUpdatedAt(LocalDateTime.now());
        return academicYear;
    }

    // ==================== STUDENT ====================

    /**
     * Create a test Student with default values.
     */
    public static Student createTestStudent() {
        return createTestStudent("TST-" + System.currentTimeMillis(), "Test", "Student");
    }

    /**
     * Create a test Student with custom details.
     */
    public static Student createTestStudent(String admissionNumber, String firstName, String lastName) {
        Student student = new Student();
        student.setAdmissionNumber(admissionNumber);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setGender("Male");
        student.setDateOfBirth(LocalDate.of(2010, 1, 15));
        student.setAdmissionDate(LocalDate.now());
        student.setCurrentGrade("10A");
        student.setStatus(StudentStatus.ACTIVE);
        student.setSchoolId(UUID.randomUUID().toString());
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        return student;
    }

    /**
     * Create a test ParentGuardian.
     */
    public static ParentGuardian createTestParentGuardian(String studentId) {
        ParentGuardian parentGuardian = new ParentGuardian();
        parentGuardian.setStudentId(studentId);
        parentGuardian.setParentName("Test Parent");
        parentGuardian.setRelationship("Father");
        parentGuardian.setPhoneNumber("987-654-3210");
        parentGuardian.setEmailAddress("parent@test.com");
        parentGuardian.setOccupation("Engineer");
        parentGuardian.setCreatedAt(LocalDateTime.now());
        parentGuardian.setUpdatedAt(LocalDateTime.now());
        return parentGuardian;
    }

    // ==================== RESET ====================

    /**
     * Reset counters for fresh test sequences.
     * Call this in setup methods if needed.
     */
    public static void reset() {
        userCounter = 1;
        schoolCounter = 1;
        studentCounter = 1;
    }
}
