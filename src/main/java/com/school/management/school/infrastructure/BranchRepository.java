package com.school.management.school.infrastructure;

import com.school.management.school.domain.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, String> {
    Page<Branch> findBySchoolIdAndDeletedFalse(String schoolId, Pageable pageable);
}
