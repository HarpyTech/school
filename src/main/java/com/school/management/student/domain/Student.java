package com.school.management.student.domain;

import com.school.management.common.entity.Address;
import com.school.management.common.entity.TenantBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document(collection = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student extends TenantBaseEntity {

    @Indexed(unique = true)
    @Field("admission_number")
    private String admissionNumber;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("gender")
    private String gender;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    @Field("admission_date")
    private LocalDate admissionDate;

    @Field("current_grade")
    private String currentGrade;

    @Field("section")
    private String section;

    @Field("status")
    private StudentStatus status = StudentStatus.APPLIED;

    @DBRef
    private ParentGuardian parent;

    private Address address;

    @Field("documents_json")
    private String documentsJson;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
