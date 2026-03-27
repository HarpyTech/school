package com.school.management.student.domain;

import com.school.management.common.entity.Address;
import com.school.management.common.entity.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "parent_guardians", indexes = {
        @Index(name = "idx_parent_school", columnList = "school_id"),
        @Index(name = "idx_parent_email", columnList = "email")
})
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
public class ParentGuardian extends TenantBaseEntity {

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 25)
    private String phone;

    @Column(name = "relationship", length = 50)
    private String relationship;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "address_street")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "state", column = @Column(name = "address_state")),
            @AttributeOverride(name = "country", column = @Column(name = "address_country")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code"))
    })
    private Address address;
}
