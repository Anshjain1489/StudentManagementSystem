package in.springproject.config;

import in.springproject.entity.Role;
import in.springproject.entity.User;
import in.springproject.entity.enums.RoleName;
import in.springproject.repository.RoleRepository;
import in.springproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Bootstraps default roles and admin user on first application startup.
 *
 * <p>This is intentionally NOT a {@code CommandLineRunner} and NOT an
 * {@code ApplicationReadyEvent} listener.  Both of those hooks run on the
 * main startup thread and would force HikariCP + Hibernate to initialise
 * eagerly — adding ~170 s to startup on Render (US→Singapore Supabase
 * round-trips) and causing the port-binding scanner to miss the open port.
 *
 * <p>Instead, {@link #run()} is called lazily from
 * {@code AuthServiceImpl.login()} on the very first login request.  By that
 * time Tomcat is already listening on its port (Render has detected the
 * service as healthy), so the slow DB round-trips happen in the background
 * of a live service.
 *
 * <p>Safe to call multiple times — all inserts are guarded by existence checks.
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
     * Seeds roles and the default admin user if they do not already exist.
     * Idempotent — safe to call multiple times.
     */
    public void run() {
        try {
            seedRoles();
            seedAdminUser();
            log.info("Database seeding completed.");
        } catch (Exception e) {
            log.error("Database seeding failed (non-fatal): {}", e.getMessage(), e);
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
