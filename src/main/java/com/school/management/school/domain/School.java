package com.school.management.school.domain;

import com.school.management.common.entity.Address;
import com.school.management.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "schools")
@Getter
@Setter
@NoArgsConstructor
public class School extends BaseEntity {

        @Field("name")
        private String name;

        @Indexed(unique = true)
        @Field("code")
        private String code;

        @Field("email")
        private String email;

        @Field("phone")
        private String phone;

        private Address address;

        @Field("active")
        private boolean active = true;
}
