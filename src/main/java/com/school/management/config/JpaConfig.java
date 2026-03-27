package com.school.management.config;

import com.school.management.common.audit.AuditAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableJpaRepositories(basePackages = "com.school.management")
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditAwareImpl() {
        return new AuditAwareImpl();
    }
}
