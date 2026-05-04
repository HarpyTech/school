package com.school.management.school.infrastructure;

import com.school.management.school.domain.AcademicYear;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AcademicYearRepository extends MongoRepository<AcademicYear, String> {
    List<AcademicYear> findBySchoolIdAndDeletedFalseOrderByStartDateDesc(String schoolId);
}
