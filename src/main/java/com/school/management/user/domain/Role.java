package com.school.management.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

/**
 * Role document — each role maps to a {@link RoleName} enum value.
 */
@Document(collection = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("name")
    private RoleName name;

    @Field("description")
    private String description;

    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public Role(RoleName name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }
}
