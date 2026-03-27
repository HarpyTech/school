package com.school.management.school.domain;

import com.school.management.common.entity.Address;
import com.school.management.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "schools", indexes = {
        @Index(name = "idx_school_name", columnList = "name"),
        @Index(name = "idx_school_code", columnList = "code", unique = true)
})
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
public class School extends BaseEntity {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "code", length = 30, nullable = false, unique = true)
    private String code;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 25)
    private String phone;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "address_street")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "state", column = @Column(name = "address_state")),
            @AttributeOverride(name = "country", column = @Column(name = "address_country")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code"))
    })
    private Address address;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
