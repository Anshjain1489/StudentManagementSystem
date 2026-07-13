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
    private final in.springproject.repository.DepartmentRepository departmentRepository;
    private final in.springproject.repository.SemesterRepository semesterRepository;
    private final in.springproject.repository.ClassroomRepository classroomRepository;
    private final in.springproject.repository.CourseRepository courseRepository;
    private final in.springproject.repository.ExamRepository examRepository;

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
            seedDepartments();
            seedSemesters();
            seedClassrooms();
            seedCourses();
            seedExams();
            log.info("Database seeding completed.");
        } catch (Exception e) {
            log.error("Database seeding failed (non-fatal): {}", e.getMessage(), e);
        }
    }

    private void seedSemesters() {
        if (semesterRepository.count() == 0) {
            semesterRepository.save(
                in.springproject.entity.Semester.builder()
                    .name("Fall 2026")
                    .startDate(java.time.LocalDate.of(2026, 8, 1))
                    .endDate(java.time.LocalDate.of(2026, 12, 15))
                    .isActive(true)
                    .academicYear("2026-2027")
                    .build()
            );
            log.info("Seeded default semester");
        }
    }

    private void seedClassrooms() {
        if (classroomRepository.count() == 0) {
            classroomRepository.save(
                in.springproject.entity.Classroom.builder()
                    .roomNumber("Room 301")
                    .building("Science Block")
                    .floor(3)
                    .capacity(60)
                    .hasProjector(true)
                    .hasAc(true)
                    .isAvailable(true)
                    .build()
            );
            log.info("Seeded default classroom");
        }
    }

    private void seedCourses() {
        if (courseRepository.countActive() == 0) {
            in.springproject.entity.Department cse = departmentRepository.findByCode("CSE").orElse(null);
            if (cse != null) {
                courseRepository.save(
                    in.springproject.entity.Course.builder()
                        .name("Introduction to Computer Programming")
                        .code("CS101")
                        .description("Learn programming concepts using Java")
                        .credits(4)
                        .maxStudents(100)
                        .isActive(true)
                        .department(cse)
                        .build()
                );
                courseRepository.save(
                    in.springproject.entity.Course.builder()
                        .name("Database Management Systems")
                        .code("CS302")
                        .description("Fundamentals of relational databases and SQL")
                        .credits(4)
                        .maxStudents(80)
                        .isActive(true)
                        .department(cse)
                        .build()
                );
                log.info("Seeded default courses");
            }
        }
    }

    private void seedExams() {
        if (examRepository.count() == 0) {
            in.springproject.entity.Course course = courseRepository.findByCode("CS101").orElse(null);
            in.springproject.entity.Semester semester = semesterRepository.findAll().stream().findFirst().orElse(null);
            in.springproject.entity.Classroom classroom = classroomRepository.findAll().stream().findFirst().orElse(null);

            if (course != null) {
                examRepository.save(
                    in.springproject.entity.Exam.builder()
                        .name("Midterm Exam - CS101")
                        .examDate(java.time.LocalDate.of(2026, 10, 15))
                        .startTime(java.time.LocalTime.of(10, 0))
                        .endTime(java.time.LocalTime.of(12, 0))
                        .totalMarks(100)
                        .passingMarks(40)
                        .description("Midterm examination covering first half of CS101")
                        .course(course)
                        .semester(semester)
                        .classroom(classroom)
                        .build()
                );
                examRepository.save(
                    in.springproject.entity.Exam.builder()
                        .name("End Semester Exam - CS101")
                        .examDate(java.time.LocalDate.of(2026, 12, 10))
                        .startTime(java.time.LocalTime.of(14, 0))
                        .endTime(java.time.LocalTime.of(17, 0))
                        .totalMarks(100)
                        .passingMarks(40)
                        .description("Final comprehensive examination of CS101")
                        .course(course)
                        .semester(semester)
                        .classroom(classroom)
                        .build()
                );
                log.info("Seeded default exams");
            }
        }
    }

    private void seedDepartments() {

        if (departmentRepository.countActive() == 0) {
            departmentRepository.save(
                in.springproject.entity.Department.builder()
                    .name("Computer Science & Engineering")
                    .code("CSE")
                    .description("Department of Computer Science and Engineering")
                    .establishedYear(2020)
                    .build()
            );
            departmentRepository.save(
                in.springproject.entity.Department.builder()
                    .name("Electronics & Communication Engineering")
                    .code("ECE")
                    .description("Department of Electronics and Communication Engineering")
                    .establishedYear(2020)
                    .build()
            );
            departmentRepository.save(
                in.springproject.entity.Department.builder()
                    .name("Mechanical Engineering")
                    .code("ME")
                    .description("Department of Mechanical Engineering")
                    .establishedYear(2021)
                    .build()
            );
            log.info("Seeded default departments: CSE, ECE, ME");
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
