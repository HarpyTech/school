package com.school.management.user.application.mapper;

import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    UserResponse toResponse(User user);

    default Set<RoleName> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}
