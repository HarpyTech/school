package com.school.management.school.application.service;

import com.school.management.common.entity.Address;
import com.school.management.common.exception.DuplicateResourceException;
import com.school.management.common.exception.ResourceNotFoundException;
import com.school.management.common.response.PagedResponse;
import com.school.management.school.application.dto.request.CreateSchoolRequest;
import com.school.management.school.application.dto.response.SchoolResponse;
import com.school.management.school.application.mapper.SchoolMapper;
import com.school.management.school.domain.School;
import com.school.management.school.infrastructure.SchoolRepository;
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
        schoolRepository.save(school);
    }
}
