package com.school.management.common.config;

import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import com.school.management.user.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role(roleName);
                role.setDescription(roleName.name());
                roleRepository.save(role);
                log.info("Seeded role: {}", roleName);
            }
        }
    }
}
