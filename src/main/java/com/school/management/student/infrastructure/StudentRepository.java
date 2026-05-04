package com.school.management.student.infrastructure;

import com.school.management.student.domain.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String>, StudentRepositoryCustom {

    boolean existsByAdmissionNumber(String admissionNumber);
}
