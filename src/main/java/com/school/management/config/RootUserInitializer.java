package com.school.management.config;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RootUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.root-user.enabled:true}")
    private boolean enabled;

    @Value("${app.bootstrap.root-user.username:root}")
    private String username;

    @Value("${app.bootstrap.root-user.email:root@school.local}")
    private String email;

    @Value("${app.bootstrap.root-user.password:RootAdmin@123}")
    private String password;

    @Value("${app.bootstrap.root-user.first-name:Root}")
    private String firstName;

    @Value("${app.bootstrap.root-user.last-name:Administrator}")
    private String lastName;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }

        String normalizedEmail = email.toLowerCase();
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(normalizedEmail)) {
            log.info("Root user bootstrap skipped because configured root account already exists");
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role is required before bootstrapping root user"));

        User user = new User();
        user.setUsername(username);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setSchoolId(null);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);
        user.setRoles(Set.of(adminRole));

        userRepository.save(user);
        log.info("Root user bootstrapped with username '{}' and email '{}'", username, normalizedEmail);
    }
}