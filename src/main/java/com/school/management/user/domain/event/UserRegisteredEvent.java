package com.school.management.user.domain.event;

import com.school.management.common.event.DomainEvent;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class UserRegisteredEvent implements DomainEvent {
    String eventId;
    String eventType;
    LocalDateTime occurredOn;

    String userId;
    String schoolId;
    String email;
    String username;
    Set<String> roles;
}
