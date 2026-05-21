package com.school.management.common.config;

import com.school.management.user.domain.Role;
import com.school.management.user.domain.RoleName;
import com.school.management.user.domain.User;
import com.school.management.user.domain.UserStatus;
import com.school.management.school.domain.School;
import com.school.management.school.infrastructure.SchoolRepository;
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
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.username:#{null}}")
    private String bootstrapAdminUsername;

    @Value("${app.bootstrap.admin.email:#{null}}")
    private String bootstrapAdminEmail;

    @Value("${app.bootstrap.admin.password:#{null}}")
    private String bootstrapAdminPassword;

    @Value("${app.bootstrap.incident-fixtures-enabled:false}")
    private boolean incidentFixturesEnabled;

    @Value("${app.bootstrap.fixture.student.username:student.demo}")
    private String fixtureStudentUsername;

    @Value("${app.bootstrap.fixture.student.email:student.demo@school.edu}")
    private String fixtureStudentEmail;

    @Value("${app.bootstrap.fixture.student.password:Student@1234}")
    private String fixtureStudentPassword;

    @Override
    public void run(ApplicationArguments args) {
        int rolesCreated = seedRoles();
        boolean adminCreated = seedBootstrapAdmin();
        SeedSummary incidentSummary = seedIncidentFixtures();

        log.info(
                "Bootstrap seed summary -> rolesCreated={}, adminCreated={}, incidentFixturesEnabled={}, schoolsCreated={}, studentCreated={}",
                rolesCreated,
                adminCreated,
                incidentSummary.enabled(),
                incidentSummary.schoolsCreated(),
                incidentSummary.studentCreated());
    }

    private int seedRoles() {
        int created = 0;
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role(roleName);
                role.setDescription(roleName.name());
                roleRepository.save(role);
                log.info("Seeded role: {}", roleName);
                created++;
            }
        }
        return created;
    }

    private boolean seedBootstrapAdmin() {
        if (!StringUtils.hasText(bootstrapAdminUsername)
                || !StringUtils.hasText(bootstrapAdminEmail)
                || !StringUtils.hasText(bootstrapAdminPassword)) {
            return false; // env vars not set — skip silently
        }

        if (userRepository.existsByEmail(bootstrapAdminEmail)) {
            log.debug("Bootstrap admin already exists, skipping.");
            return false;
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
        return true;
    }

    private SeedSummary seedIncidentFixtures() {
        if (!incidentFixturesEnabled) {
            return SeedSummary.disabled();
        }

        int schoolsCreated = 0;
        if (seedSchool("sch-001", "Demo School", "DEMO", "demo@school.edu")) {
            schoolsCreated++;
        }
        if (seedSchool("sch-002", "North Ridge School", "NRG", "northridge@school.edu")) {
            schoolsCreated++;
        }
        if (seedSchool("sch-003", "South Valley School", "SVS", "southvalley@school.edu")) {
            schoolsCreated++;
        }

        boolean studentCreated = seedStudentFixture();
        return new SeedSummary(schoolsCreated, studentCreated, true);
    }

    private boolean seedSchool(String id, String name, String code, String email) {
        if (schoolRepository.existsById(id)) {
            return false;
        }

        School school = new School();
        school.setId(id);
        school.setName(name);
        school.setCode(code);
        school.setEmail(email);
        school.setActive(true);
        school.setDeleted(false);

        schoolRepository.save(school);
        log.info("Seeded incident fixture school: {} ({})", id, code);
        return true;
    }

    private boolean seedStudentFixture() {
        if (!StringUtils.hasText(fixtureStudentUsername)
                || !StringUtils.hasText(fixtureStudentEmail)
                || !StringUtils.hasText(fixtureStudentPassword)) {
            return false;
        }

        if (userRepository.existsByEmail(fixtureStudentEmail)) {
            return false;
        }

        User student = new User();
        student.setUsername(fixtureStudentUsername);
        student.setEmail(fixtureStudentEmail.toLowerCase());
        student.setPassword(passwordEncoder.encode(fixtureStudentPassword));
        student.setFirstName("Fixture");
        student.setLastName("Student");
        student.setSchoolId("sch-002");
        student.setStatus(UserStatus.ACTIVE);
        student.setEmailVerified(true);
        student.setRoles(Set.of(RoleName.STUDENT));

        userRepository.save(student);
        log.info("Seeded incident fixture student user: {}", fixtureStudentEmail);
        return true;
    }

    private record SeedSummary(int schoolsCreated, boolean studentCreated, boolean enabled) {
        private static SeedSummary disabled() {
            return new SeedSummary(0, false, false);
        }
    }
}
