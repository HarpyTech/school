package com.school.management.school.domain;

import com.school.management.common.entity.Address;
import com.school.management.common.entity.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "school_branches", indexes = {
        @Index(name = "idx_branch_school_id", columnList = "school_id"),
        @Index(name = "idx_branch_code", columnList = "code")
})
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
public class Branch extends TenantBaseEntity {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "code", length = 30, nullable = false)
    private String code;

    @Column(name = "principal_name", length = 150)
    private String principalName;

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
