package com.school.management.student.infrastructure;

import com.school.management.student.domain.ParentGuardian;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ParentGuardianRepository extends MongoRepository<ParentGuardian, String> {
    Optional<ParentGuardian> findBySchoolIdAndEmailAndDeletedFalse(String schoolId, String email);
}
