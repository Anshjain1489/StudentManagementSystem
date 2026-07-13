package in.springproject.config;

import in.springproject.entity.Role;
import in.springproject.entity.User;
import in.springproject.entity.enums.RoleName;
import in.springproject.repository.RoleRepository;
import in.springproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Bootstraps default roles and admin user on first application startup.
 * Safe to run repeatedly — uses existence checks before inserting.
 */
@Component
@org.springframework.context.annotation.Lazy(false)
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@sms.edu}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@1234}")
    private String adminPassword;

    @Value("${app.admin.firstName:System}")
    private String adminFirstName;

    @Value("${app.admin.lastName:Admin}")
    private String adminLastName;

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(
                    Role.builder()
                        .name(roleName)
                        .description(roleName.name() + " role")
                        .build()
                );
                log.info("Seeded role: {}", roleName);
            }
        }
    }

    private void seedAdminUser() {
        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found after seeding"));

            User admin = User.builder()
                .username(adminFirstName.toLowerCase() + "." + adminLastName.toLowerCase())
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .isActive(true)
                .isEmailVerified(true)
                .roles(Set.of(adminRole))
                .build();

            userRepository.save(admin);
            log.info("Admin user created: {}", adminEmail);
        }
    }
}
