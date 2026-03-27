package com.school.management.student.domain.event;

import com.school.management.common.event.DomainEvent;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class StudentAdmittedEvent implements DomainEvent {
    String eventId;
    String eventType;
    LocalDateTime occurredOn;

    String studentId;
    String schoolId;
    String admissionNumber;
    String fullName;
    String grade;
}
