package com.school.management.school.application.mapper;

import com.school.management.school.application.dto.response.SchoolResponse;
import com.school.management.school.domain.School;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SchoolMapper {

    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "state", source = "address.state")
    @Mapping(target = "country", source = "address.country")
    SchoolResponse toResponse(School school);
}
