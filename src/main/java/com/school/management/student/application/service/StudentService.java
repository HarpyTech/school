package com.school.management.student.application.service;

import com.school.management.common.constants.AppConstants;
import com.school.management.common.entity.Address;
import com.school.management.common.exception.BusinessException;
import com.school.management.common.exception.ResourceNotFoundException;
import com.school.management.common.response.PagedResponse;
import com.school.management.student.application.dto.request.CreateStudentRequest;
import com.school.management.student.application.dto.request.PromoteStudentRequest;
import com.school.management.student.application.dto.response.StudentResponse;
import com.school.management.student.application.mapper.StudentMapper;
import com.school.management.student.domain.ParentGuardian;
import com.school.management.student.domain.Student;
import com.school.management.student.domain.StudentStatus;
import com.school.management.student.infrastructure.ParentGuardianRepository;
import com.school.management.student.infrastructure.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final ParentGuardianRepository parentRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentResponse admit(CreateStudentRequest request) {
        Student student = new Student();
        student.setSchoolId(request.schoolId());
        student.setAdmissionNumber(generateAdmissionNumber(request.schoolId()));
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setGender(request.gender());
        student.setDateOfBirth(request.dateOfBirth());
        student.setAdmissionDate(request.admissionDate());
        student.setCurrentGrade(request.currentGrade());
        student.setSection(request.section());
        student.setStatus(StudentStatus.ADMITTED);
        student.setDocumentsJson(request.documentsJson());
        student.setAddress(Address.builder()
                .street(request.street())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .zipCode(request.zipCode())
                .build());

        ParentGuardian parent = findOrCreateParent(request);
        student.setParent(parent);

        Student saved = studentRepository.save(student);

        return studentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public StudentResponse getById(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public PagedResponse<StudentResponse> search(String schoolId, StudentStatus status, String search, int page, int size) {
        var result = studentRepository.search(
                        schoolId,
                        status,
                        search,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(studentMapper::toResponse);

        return PagedResponse.of(result);
    }

    @Transactional
    public StudentResponse promote(String id, PromoteStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        if (student.getStatus() == StudentStatus.DROPPED_OUT || student.getStatus() == StudentStatus.TRANSFERRED) {
            throw new BusinessException("Cannot promote a transferred or dropped out student");
        }

        student.setCurrentGrade(request.nextGrade());
        student.setSection(request.nextSection());
        student.setStatus(StudentStatus.ACTIVE);

        Student saved = studentRepository.save(student);

        return studentMapper.toResponse(saved);
    }

    @Transactional
    public StudentResponse transfer(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        student.setStatus(StudentStatus.TRANSFERRED);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse dropOut(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        student.setStatus(StudentStatus.DROPPED_OUT);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    private ParentGuardian findOrCreateParent(CreateStudentRequest request) {
        if (request.parentEmail() != null && !request.parentEmail().isBlank()) {
            return parentRepository.findBySchoolIdAndEmailAndDeletedFalse(request.schoolId(), request.parentEmail())
                    .orElseGet(() -> createParent(request));
        }
        return createParent(request);
    }

    private ParentGuardian createParent(CreateStudentRequest request) {
        ParentGuardian parent = new ParentGuardian();
        parent.setSchoolId(request.schoolId());
        parent.setFirstName(request.parentFirstName());
        parent.setLastName(request.parentLastName());
        parent.setEmail(request.parentEmail());
        parent.setPhone(request.parentPhone());
        parent.setRelationship(request.relationship());
        parent.setAddress(Address.builder()
                .street(request.street())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .zipCode(request.zipCode())
                .build());
        return parentRepository.save(parent);
    }

    private String generateAdmissionNumber(String schoolId) {
        // Example: ADM-<schoolSuffix>-<timestamp>
        String schoolSuffix = schoolId == null || schoolId.length() < 4
                ? "GEN"
                : schoolId.substring(0, 4).toUpperCase();
        return "ADM-" + schoolSuffix + "-" + System.currentTimeMillis();
    }
}
