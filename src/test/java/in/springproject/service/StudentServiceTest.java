package in.springproject.service;

import in.springproject.dto.student.StudentRequest;
import in.springproject.dto.student.StudentResponse;
import in.springproject.entity.Department;
import in.springproject.entity.Student;
import in.springproject.entity.enums.Gender;
import in.springproject.exception.DuplicateResourceException;
import in.springproject.exception.ResourceNotFoundException;
import in.springproject.repository.CourseRepository;
import in.springproject.repository.DepartmentRepository;
import in.springproject.repository.StudentRepository;
import in.springproject.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private StorageService storageService;

    @InjectMocks private StudentServiceImpl studentService;

    private Department testDepartment;
    private Student testStudent;
    private StudentRequest studentRequest;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
            .name("Computer Science").code("CS").build();
        testDepartment.setId(1L);
        testDepartment.setDeleted(false);

        testStudent = Student.builder()
            .rollNumber("CS2024001")
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@student.edu")
            .phone("9876543210")
            .gender(Gender.MALE)
            .enrollmentDate(LocalDate.now())
            .department(testDepartment)
            .build();
        testStudent.setId(1L);
        testStudent.setDeleted(false);

        studentRequest = new StudentRequest();
        studentRequest.setRollNumber("CS2024001");
        studentRequest.setFirstName("John");
        studentRequest.setLastName("Doe");
        studentRequest.setEmail("john.doe@student.edu");
        studentRequest.setPhone("9876543210");
        studentRequest.setGender(Gender.MALE);
        studentRequest.setDepartmentId(1L);
    }

    @Nested
    @DisplayName("Create Student Tests")
    class CreateStudentTests {

        @Test
        @DisplayName("Should create student successfully")
        void shouldCreateStudentSuccessfully() {
            when(studentRepository.existsByRollNumber(anyString())).thenReturn(false);
            when(studentRepository.existsByEmail(anyString())).thenReturn(false);
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

            StudentResponse response = studentService.createStudent(studentRequest);

            assertThat(response).isNotNull();
            assertThat(response.getFirstName()).isEqualTo("John");
            assertThat(response.getLastName()).isEqualTo("Doe");
            assertThat(response.getRollNumber()).isEqualTo("CS2024001");
            verify(studentRepository).save(any(Student.class));
        }

        @Test
        @DisplayName("Should throw exception when roll number already exists")
        void shouldThrowExceptionWhenRollNumberExists() {
            when(studentRepository.existsByRollNumber("CS2024001")).thenReturn(true);

            assertThatThrownBy(() -> studentService.createStudent(studentRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Student already exists with roll number");

            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(studentRepository.existsByRollNumber(anyString())).thenReturn(false);
            when(studentRepository.existsByEmail("john.doe@student.edu")).thenReturn(true);

            assertThatThrownBy(() -> studentService.createStudent(studentRequest))
                .isInstanceOf(DuplicateResourceException.class);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when department not found")
        void shouldThrowExceptionWhenDepartmentNotFound() {
            when(studentRepository.existsByRollNumber(anyString())).thenReturn(false);
            when(studentRepository.existsByEmail(anyString())).thenReturn(false);
            when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.createStudent(studentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Department");
        }
    }

    @Nested
    @DisplayName("Get Student Tests")
    class GetStudentTests {

        @Test
        @DisplayName("Should get student by ID successfully")
        void shouldGetStudentByIdSuccessfully() {
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

            StudentResponse response = studentService.getStudentById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getFullName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when student not found")
        void shouldThrowExceptionWhenStudentNotFound() {
            when(studentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.getStudentById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student");
        }

        @Test
        @DisplayName("Should get all students with pagination")
        void shouldGetAllStudentsWithPagination() {
            Page<Student> studentPage = new PageImpl<>(List.of(testStudent));
            Pageable pageable = PageRequest.of(0, 10);
            when(studentRepository.findAllActive(pageable)).thenReturn(studentPage);

            var response = studentService.getAllStudents(pageable);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Delete Student Tests")
    class DeleteStudentTests {

        @Test
        @DisplayName("Should soft delete student successfully")
        void shouldSoftDeleteStudentSuccessfully() {
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

            studentService.deleteStudent(1L);

            verify(studentRepository).save(argThat(s -> Boolean.TRUE.equals(s.getDeleted())));
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent student")
        void shouldThrowExceptionWhenDeletingNonExistentStudent() {
            when(studentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.deleteStudent(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
