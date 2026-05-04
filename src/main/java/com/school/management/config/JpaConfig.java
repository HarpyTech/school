package com.school.management.config;

import com.school.management.common.entity.BaseEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditAwareImpl")
@EnableMongoRepositories(basePackages = "com.school.management")
public class JpaConfig {

    @Bean
    public AbstractMongoEventListener<BaseEntity> baseEntityMongoEventListener() {
        return new AbstractMongoEventListener<>() {
            @Override
            public void onBeforeConvert(BeforeConvertEvent<BaseEntity> event) {
                event.getSource().onPrePersist();
            }
        };
    }
}
