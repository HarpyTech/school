package com.school.management.school.infrastructure;

import com.school.management.school.domain.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, String> {
    List<AcademicYear> findBySchoolIdAndDeletedFalseOrderByStartDateDesc(String schoolId);
}
