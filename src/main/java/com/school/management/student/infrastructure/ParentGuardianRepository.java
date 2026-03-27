package com.school.management.student.infrastructure;

import com.school.management.student.domain.ParentGuardian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentGuardianRepository extends JpaRepository<ParentGuardian, String> {
    Optional<ParentGuardian> findBySchoolIdAndEmailAndDeletedFalse(String schoolId, String email);
}
