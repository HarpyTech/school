package com.school.management.student.application.service;

import com.school.management.common.exception.BusinessException;
import com.school.management.student.application.dto.request.PromoteStudentRequest;
import com.school.management.student.application.mapper.StudentMapper;
import com.school.management.student.domain.Student;
import com.school.management.student.domain.StudentStatus;
import com.school.management.student.infrastructure.ParentGuardianRepository;
import com.school.management.student.infrastructure.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private ParentGuardianRepository parentGuardianRepository;
    @Mock private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    @Test
    void promote_shouldThrowForTransferredStudent() {
        Student student = new Student();
        student.setId("s1");
        student.setStatus(StudentStatus.TRANSFERRED);

        when(studentRepository.findById("s1")).thenReturn(Optional.of(student));

        assertThrows(BusinessException.class,
                () -> studentService.promote("s1", new PromoteStudentRequest("Grade-9", "A")));
    }
}
