package com.school.management.school.infrastructure;

import com.school.management.school.domain.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, String> {
    boolean existsByCode(String code);
    Page<School> findByDeletedFalse(Pageable pageable);
}
