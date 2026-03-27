package com.school.management.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base entity for all tenant-scoped (school-specific) entities.
 * Every entity in a multi-tenant context must carry schoolId.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class TenantBaseEntity extends BaseEntity {

    @Column(name = "school_id", length = 36, nullable = false)
    private String schoolId;
}
