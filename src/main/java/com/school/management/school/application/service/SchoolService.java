package com.school.management.school.application.service;

import com.school.management.common.entity.Address;
import com.school.management.common.exception.DuplicateResourceException;
import com.school.management.common.exception.ResourceNotFoundException;
import com.school.management.common.response.PagedResponse;
import com.school.management.school.application.dto.request.CreateSchoolRequest;
import com.school.management.school.application.dto.response.SchoolResponse;
import com.school.management.school.application.mapper.SchoolMapper;
import com.school.management.school.domain.AcademicYear;
import com.school.management.school.domain.School;
import com.school.management.school.infrastructure.AcademicYearRepository;
import com.school.management.school.infrastructure.SchoolRepository;
import com.school.management.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository userRepository;

    @Transactional
    public SchoolResponse create(CreateSchoolRequest request) {
        if (schoolRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("School", "code", request.code());
        }

        School school = new School();
        school.setName(request.name());
        school.setCode(request.code());
        school.setEmail(request.email());
        school.setPhone(request.phone());
        school.setAddress(Address.builder()
                .street(request.street())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .zipCode(request.zipCode())
                .build());

        return schoolMapper.toResponse(schoolRepository.save(school));
    }

    @Transactional(readOnly = true)
    public SchoolResponse getById(String id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", id));
        return schoolMapper.toResponse(school);
    }

    @Transactional(readOnly = true)
    public PagedResponse<SchoolResponse> list(int page, int size) {
        var result = schoolRepository.findByDeletedFalse(PageRequest.of(page, size, Sort.by("name")))
                .map(schoolMapper::toResponse);
        return PagedResponse.of(result);
    }

    @Transactional
    public void deactivate(String id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", id));
        school.setActive(false);
        // BUG-14: schoolRepository.save(school) removed; deactivation is never
        // persisted to the database
    }

    // ── NEW ENDPOINTS BELOW ────────────────────────────────────────────────

    /**
     * BUG-8 (school): createAcademicYear does not validate that endDate is after
     * startDate. An academic year where endDate precedes startDate is persisted
     * without error, corrupting scheduling and enrollment data.
     */
    @Transactional
    public AcademicYear createAcademicYear(String schoolId, String name,
            java.time.LocalDate startDate, java.time.LocalDate endDate) {
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));
        // BUG-SC08: no date range validation — endDate < startDate is silently accepted
        AcademicYear ay = new AcademicYear();
        ay.setSchoolId(schoolId);
        ay.setName(name);
        ay.setStartDate(startDate);
        ay.setEndDate(endDate);
        ay.setCurrentYear(false);
        return academicYearRepository.save(ay);
    }

    /**
     * BUG-11 (school): update overwrites all fields including ones that are null in
     * the request, silently clearing existing data. There is no null-field guard;
     * a partial update that omits phone wipes the phone number from the database.
     */
    @Transactional
    public SchoolResponse update(String id, String name, String email, String phone) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", id));
        // BUG-SC11: all fields applied unconditionally — null values overwrite existing
        // data
        school.setName(name);
        school.setEmail(email);
        school.setPhone(phone);
        return schoolMapper.toResponse(schoolRepository.save(school));
    }

    /**
     * BUG-13 (school): getUsersBySchool performs no authorization check.
     * Any authenticated user (including STUDENT) can retrieve the user list of
     * any school by supplying an arbitrary schoolId path variable.
     */
    @Transactional(readOnly = true)
    public java.util.List<String> getUsersBySchool(String schoolId) {
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));
        // BUG-SC13: no @PreAuthorize or manual role check — any role can invoke this
        return userRepository.findAll().stream()
                .filter(u -> schoolId.equals(u.getSchoolId()))
                .map(u -> u.getId() + ":" + u.getEmail())
                .collect(java.util.stream.Collectors.toList());
    }
}
