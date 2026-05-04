package com.school.management.common.config;

import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import com.school.management.user.infrastructure.RoleRepository;
import com.school.management.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.username:#{null}}")
    private String bootstrapAdminUsername;

    @Value("${app.bootstrap.admin.email:#{null}}")
    private String bootstrapAdminEmail;

    @Value("${app.bootstrap.admin.password:#{null}}")
    private String bootstrapAdminPassword;

    @Override
    public void run(ApplicationArguments args) {
        seedRoles();
        seedBootstrapAdmin();
    }

    private void seedRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role(roleName);
                role.setDescription(roleName.name());
                roleRepository.save(role);
                log.info("Seeded role: {}", roleName);
            }
        }
    }

    private void seedBootstrapAdmin() {
        if (!StringUtils.hasText(bootstrapAdminUsername)
                || !StringUtils.hasText(bootstrapAdminEmail)
                || !StringUtils.hasText(bootstrapAdminPassword)) {
            return; // env vars not set — skip silently
        }

        if (userRepository.existsByEmail(bootstrapAdminEmail)) {
            log.debug("Bootstrap admin already exists, skipping.");
            return;
        }

        User admin = new User();
        admin.setUsername(bootstrapAdminUsername);
        admin.setEmail(bootstrapAdminEmail.toLowerCase());
        admin.setPassword(passwordEncoder.encode(bootstrapAdminPassword));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setStatus(UserStatus.ACTIVE);
        admin.setEmailVerified(true);
        admin.setRoles(Set.of(RoleName.ADMIN));

        userRepository.save(admin);
        log.info("Bootstrap admin created: {}", bootstrapAdminEmail);
    }
}
