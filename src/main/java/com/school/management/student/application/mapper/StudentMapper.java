package com.school.management.student.application.mapper;

import com.school.management.student.application.dto.response.StudentResponse;
import com.school.management.student.domain.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "parentId", expression = "java(student.getParent() != null ? student.getParent().getId() : null)")
    @Mapping(target = "parentName", expression = "java(student.getParent() != null ? student.getParent().getFirstName() + \" \" + student.getParent().getLastName() : null)")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "state", source = "address.state")
    StudentResponse toResponse(Student student);
}
