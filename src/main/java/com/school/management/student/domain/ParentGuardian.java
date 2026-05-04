package com.school.management.student.domain;

import com.school.management.common.entity.Address;
import com.school.management.common.entity.TenantBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "parent_guardians")
@Getter
@Setter
@NoArgsConstructor
public class ParentGuardian extends TenantBaseEntity {

        @Field("first_name")
        private String firstName;

        @Field("last_name")
        private String lastName;

        @Field("email")
        private String email;

        @Field("phone")
        private String phone;

        @Field("relationship")
        private String relationship;

        private Address address;
}
