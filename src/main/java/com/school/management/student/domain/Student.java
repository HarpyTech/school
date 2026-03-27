package com.school.management.student.domain;

import com.school.management.common.entity.Address;
import com.school.management.common.entity.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_student_school", columnList = "school_id"),
        @Index(name = "idx_student_admission_no", columnList = "admission_number", unique = true),
        @Index(name = "idx_student_status", columnList = "status"),
        @Index(name = "idx_student_grade", columnList = "current_grade")
})
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
public class Student extends TenantBaseEntity {

    @Column(name = "admission_number", length = 40, nullable = false, unique = true)
    private String admissionNumber;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "admission_date", nullable = false)
    private LocalDate admissionDate;

    @Column(name = "current_grade", length = 30)
    private String currentGrade;

    @Column(name = "section", length = 30)
    private String section;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", length = 30, nullable = false)
    private StudentStatus status = StudentStatus.APPLIED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ParentGuardian parent;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "address_street")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "state", column = @Column(name = "address_state")),
            @AttributeOverride(name = "country", column = @Column(name = "address_country")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code"))
    })
    private Address address;

    @Column(name = "documents_json", columnDefinition = "TEXT")
    private String documentsJson;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
