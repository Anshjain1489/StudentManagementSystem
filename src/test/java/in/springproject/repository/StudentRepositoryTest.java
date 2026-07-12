package in.springproject.repository;

import in.springproject.entity.Department;
import in.springproject.entity.Student;
import in.springproject.entity.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for StudentRepository using an H2 in-memory database.
 *
 * <p>@DataJpaTest does not load the full application context, so it does not
 * pick up @EnableJpaAuditing from the main class. We supply a minimal
 * TestConfig that re-enables auditing with a simple stub AuditorAware bean.</p>
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(StudentRepositoryTest.TestConfig.class)
@DisplayName("StudentRepository Integration Tests")
class StudentRepositoryTest {

    /**
     * Minimal test configuration: enables JPA auditing and provides
     * a stub {@link AuditorAware} that always returns "test-user".
     * This prevents NoSuchBeanDefinitionException for "auditorAwareImpl"
     * when @DataJpaTest loads only the JPA slice.
     */
    @EnableJpaAuditing(auditorAwareRef = "testAuditorAware")
    static class TestConfig {
        @Bean
        public AuditorAware<String> testAuditorAware() {
            return () -> Optional.of("test-user");
        }
    }

    @Autowired private StudentRepository studentRepository;
    @Autowired private DepartmentRepository departmentRepository;

    private Department testDepartment;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
            .name("Computer Science").code("CS").build();
        testDepartment.setDeleted(false);
        testDepartment = departmentRepository.save(testDepartment);

        testStudent = Student.builder()
            .rollNumber("CS2024001")
            .firstName("John").lastName("Doe")
            .email("john.doe@student.edu")
            .gender(Gender.MALE)
            .enrollmentDate(LocalDate.now())
            .department(testDepartment)
            .build();
        testStudent.setDeleted(false);
        testStudent = studentRepository.save(testStudent);
    }

    @Test
    @DisplayName("Should find student by roll number")
    void shouldFindByRollNumber() {
        Optional<Student> found = studentRepository.findByRollNumber("CS2024001");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Should return empty for non-existent roll number")
    void shouldReturnEmptyForNonExistentRollNumber() {
        Optional<Student> found = studentRepository.findByRollNumber("NONEXISTENT");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find student by email")
    void shouldFindByEmail() {
        Optional<Student> found = studentRepository.findByEmail("john.doe@student.edu");
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Should return true for existing roll number")
    void shouldReturnTrueForExistingRollNumber() {
        assertThat(studentRepository.existsByRollNumber("CS2024001")).isTrue();
        assertThat(studentRepository.existsByRollNumber("NONEXISTENT")).isFalse();
    }

    @Test
    @DisplayName("Should count active students")
    void shouldCountActiveStudents() {
        long count = studentRepository.countActive();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should search students by name")
    void shouldSearchStudentsByName() {
        Page<Student> results = studentRepository.searchStudents("John", PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should find students by department")
    void shouldFindStudentsByDepartment() {
        Page<Student> results = studentRepository.findByDepartmentId(testDepartment.getId(), PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(1);
    }
}
