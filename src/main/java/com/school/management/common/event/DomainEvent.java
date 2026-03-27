package com.school.management.common.event;

import java.time.LocalDateTime;

/**
 * Marker interface for all domain events published to Kafka.
 */
public interface DomainEvent {
    String getEventId();
    String getEventType();
    LocalDateTime getOccurredOn();
}
