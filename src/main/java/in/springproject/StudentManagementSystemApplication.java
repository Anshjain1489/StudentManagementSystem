package in.springproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Student Management System application.
 * <p>
 * This is an enterprise-grade SMS built with Spring Boot 3 + Java 21,
 * featuring JWT authentication, role-based access, full student lifecycle
 * management, AI-powered analytics, and report generation.
 * </p>
 *
 * @author SpringProject Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableCaching
@EnableScheduling
@EnableAsync
public class StudentManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementSystemApplication.class, args);
    }
}
