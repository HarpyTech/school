package com.school.management.common.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Base entity for all tenant-scoped (school-specific) entities.
 * Every entity in a multi-tenant context must carry schoolId.
 */
@Getter
@Setter
public abstract class TenantBaseEntity extends BaseEntity {

    @Field("school_id")
    private String schoolId;
}
