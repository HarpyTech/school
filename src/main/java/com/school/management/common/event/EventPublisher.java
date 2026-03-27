package com.school.management.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Generic Kafka event publisher used across all modules.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes an event to the given Kafka topic asynchronously.
     *
     * @param topic the Kafka topic name
     * @param key   the message key (used for partitioning)
     * @param event the event payload
     */
    public void publish(String topic, String key, Object event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event [{}] to topic [{}]: {}",
                        event.getClass().getSimpleName(), topic, ex.getMessage(), ex);
            } else {
                log.debug("Published event [{}] to topic [{}] partition [{}] offset [{}]",
                        event.getClass().getSimpleName(),
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    public void publish(String topic, Object event) {
        publish(topic, null, event);
    }
}
