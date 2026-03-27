package com.school.management.student.infrastructure;

import com.school.management.student.domain.Student;
import com.school.management.student.domain.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, String> {

    boolean existsByAdmissionNumber(String admissionNumber);

    @Query("""
            SELECT s FROM Student s
            WHERE s.deleted = false
              AND s.schoolId = :schoolId
              AND (:status IS NULL OR s.status = :status)
              AND (
                    :search IS NULL OR :search = ''
                    OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            """)
    Page<Student> search(
            @Param("schoolId") String schoolId,
            @Param("status") StudentStatus status,
            @Param("search") String search,
            Pageable pageable
    );
}
