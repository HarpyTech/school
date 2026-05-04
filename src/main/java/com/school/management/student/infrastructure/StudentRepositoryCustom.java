package com.school.management.student.infrastructure;

import com.school.management.student.domain.Student;
import com.school.management.student.domain.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentRepositoryCustom {

    Page<Student> search(String schoolId, StudentStatus status, String search, Pageable pageable);
}
