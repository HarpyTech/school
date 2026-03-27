package com.school.management.school.domain;

import com.school.management.common.entity.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "academic_years", indexes = {
        @Index(name = "idx_ay_school_id", columnList = "school_id"),
        @Index(name = "idx_ay_name", columnList = "name")
})
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
public class AcademicYear extends TenantBaseEntity {

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "current_year", nullable = false)
    private boolean currentYear;
}
