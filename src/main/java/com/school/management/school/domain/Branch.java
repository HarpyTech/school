package com.school.management.school.domain;

import com.school.management.common.entity.Address;
import com.school.management.common.entity.TenantBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "school_branches")
@Getter
@Setter
@NoArgsConstructor
public class Branch extends TenantBaseEntity {

        @Field("name")
        private String name;

        @Field("code")
        private String code;

        @Field("principal_name")
        private String principalName;

        private Address address;

        @Field("active")
        private boolean active = true;
}
