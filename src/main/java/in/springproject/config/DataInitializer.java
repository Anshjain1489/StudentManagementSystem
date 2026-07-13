package in.springproject.config;

import in.springproject.entity.Role;
import in.springproject.entity.User;
import in.springproject.entity.enums.RoleName;
import in.springproject.repository.RoleRepository;
import in.springproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Bootstraps default roles and admin user on first application startup.
 * Uses ApplicationReadyEvent so seeding runs AFTER Tomcat has fully bound to
 * its port (allowing Render/cloud platforms to detect the service as healthy
 * before the DB round-trips complete).
 * Safe to run repeatedly — uses existence checks before inserting.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@sms.edu}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.admin.firstName:System}")
    private String adminFirstName;

    @Value("${app.admin.lastName:Administrator}")
    private String adminLastName;

    /**
     * Triggered after the application context is fully refreshed and Tomcat
     * has bound to its port. Database seeding happens here so the port is
     * already open when cloud health checks run.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application ready — starting database seeding...");
        try {
            seedRoles();
            seedAdminUser();
            log.info("Database seeding completed successfully.");
        } catch (Exception e) {
            log.error("Database seeding failed (non-fatal): {}", e.getMessage(), e);
        }
    }

    /**
     * Public entry point for on-demand seeding (e.g., called from AuthService on first login).
     * Idempotent — safe to call multiple times.
     */
    public void run() {
        try {
            seedRoles();
            seedAdminUser();
        } catch (Exception e) {
            log.error("On-demand seeding error: {}", e.getMessage(), e);
        }
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
