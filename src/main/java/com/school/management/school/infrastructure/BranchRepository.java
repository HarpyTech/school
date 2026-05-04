package com.school.management.school.infrastructure;

import com.school.management.school.domain.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BranchRepository extends MongoRepository<Branch, String> {
    Page<Branch> findBySchoolIdAndDeletedFalse(String schoolId, Pageable pageable);
}
