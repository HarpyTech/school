package com.school.management.user.application.mapper;

import com.school.management.user.application.dto.response.UserResponse;
import com.school.management.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "phoneNumber", source = "phoneNumber")
    UserResponse toResponse(User user);
}
