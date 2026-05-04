package com.school.management.school.domain;

import com.school.management.common.entity.TenantBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document(collection = "academic_years")
@Getter
@Setter
@NoArgsConstructor
public class AcademicYear extends TenantBaseEntity {

    @Field("name")
    private String name;

    @Field("start_date")
    private LocalDate startDate;

    @Field("end_date")
    private LocalDate endDate;

    @Field("current_year")
    private boolean currentYear;
}
