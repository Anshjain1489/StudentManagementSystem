package in.springproject.service.impl;

import in.springproject.dto.student.*;
import in.springproject.entity.*;
import in.springproject.exception.*;
import in.springproject.repository.*;
import in.springproject.service.StorageService;
import in.springproject.service.StudentService;
import in.springproject.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of {@link StudentService}.
 * <p>
 * Provides transactional CRUD, photo upload, and course enrollment
 * operations for the {@link Student} entity.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final StorageService storageService;

    /**
     * {@inheritDoc}
     * <p>Validates uniqueness of roll number and email before persisting.</p>
     */
    @Override
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByRollNumber(request.getRollNumber())) {
            throw new DuplicateResourceException("Student", "roll number", request.getRollNumber());
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Student", "email", request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        Student student = Student.builder()
            .rollNumber(request.getRollNumber())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .address(request.getAddress())
            .city(request.getCity())
            .state(request.getState())
            .pincode(request.getPincode())
            .enrollmentDate(request.getEnrollmentDate())
            .currentSemester(request.getCurrentSemester())
            .bloodGroup(request.getBloodGroup())
            .emergencyContact(request.getEmergencyContact())
            .department(department)
            .build();

        Student saved = studentRepository.save(student);
        log.info("Created student: {} ({})", saved.getFullName(), saved.getRollNumber());
        return mapToResponse(saved);
    }

    /**
     * {@inheritDoc}
     * <p>Allows roll number change only if the new roll number is not taken by another student.</p>
     */
    @Override
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = getStudentEntityById(id);

        if (!student.getRollNumber().equals(request.getRollNumber()) &&
            studentRepository.existsByRollNumber(request.getRollNumber())) {
            throw new DuplicateResourceException("Student", "roll number", request.getRollNumber());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        student.setRollNumber(request.getRollNumber());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setAddress(request.getAddress());
        student.setCity(request.getCity());
        student.setState(request.getState());
        student.setPincode(request.getPincode());
        student.setCurrentSemester(request.getCurrentSemester());
        student.setBloodGroup(request.getBloodGroup());
        student.setEmergencyContact(request.getEmergencyContact());
        student.setDepartment(department);

        return mapToResponse(studentRepository.save(student));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        return mapToResponse(getStudentEntityById(id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentByRollNumber(String rollNumber) {
        Student student = studentRepository.findByRollNumber(rollNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "roll number", rollNumber));
        return mapToResponse(student);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getAllStudents(Pageable pageable) {
        Page<StudentResponse> page = studentRepository.findAllActive(pageable).map(this::mapToResponse);
        return PageResponse.from(page);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> searchStudents(String query, Pageable pageable) {
        Page<StudentResponse> page = studentRepository.searchStudents(query, pageable).map(this::mapToResponse);
        return PageResponse.from(page);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getStudentsByDepartment(Long departmentId, Pageable pageable) {
        Page<StudentResponse> page = studentRepository.findByDepartmentId(departmentId, pageable).map(this::mapToResponse);
        return PageResponse.from(page);
    }

    /**
     * {@inheritDoc}
     * <p>Marks the student as deleted (soft delete) without removing the DB record.</p>
     */
    @Override
    public void deleteStudent(Long id) {
        Student student = getStudentEntityById(id);
        student.setDeleted(true);
        studentRepository.save(student);
        log.info("Soft deleted student: {}", id);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates file storage to {@link StorageService} and persists the returned URL.</p>
     */
    @Override
    public String uploadPhoto(Long id, MultipartFile file) {
        Student student = getStudentEntityById(id);
        String photoUrl = storageService.storeFile(file, "students/photos");
        student.setPhotoUrl(photoUrl);
        studentRepository.save(student);
        return photoUrl;
    }

    /** {@inheritDoc} */
    @Override
    public void enrollInCourse(Long studentId, Long courseId) {
        Student student = getStudentEntityById(studentId);
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
        student.getCourses().add(course);
        studentRepository.save(student);
        log.info("Student {} enrolled in course {}", studentId, courseId);
    }

    /** {@inheritDoc} */
    @Override
    public void unenrollFromCourse(Long studentId, Long courseId) {
        Student student = getStudentEntityById(studentId);
        student.getCourses().removeIf(c -> c.getId().equals(courseId));
        studentRepository.save(student);
        log.info("Student {} unenrolled from course {}", studentId, courseId);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public long countStudents() {
        return studentRepository.countActive();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Loads a non-deleted {@link Student} by ID or throws {@link ResourceNotFoundException}.
     *
     * @param id the student's primary key
     * @return the student entity
     */
    private Student getStudentEntityById(Long id) {
        return studentRepository.findById(id)
            .filter(s -> !Boolean.TRUE.equals(s.getDeleted()))
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    /**
     * Maps a {@link Student} entity to its corresponding {@link StudentResponse} DTO.
     *
     * @param student the entity to map
     * @return the populated response DTO
     */
    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
            .id(student.getId())
            .rollNumber(student.getRollNumber())
            .firstName(student.getFirstName())
            .lastName(student.getLastName())
            .fullName(student.getFirstName() + " " + student.getLastName())
            .email(student.getEmail())
            .phone(student.getPhone())
            .dateOfBirth(student.getDateOfBirth())
            .gender(student.getGender())
            .address(student.getAddress())
            .city(student.getCity())
            .state(student.getState())
            .pincode(student.getPincode())
            .photoUrl(student.getPhotoUrl())
            .enrollmentDate(student.getEnrollmentDate())
            .graduationDate(student.getGraduationDate())
            .currentSemester(student.getCurrentSemester())
            .cgpa(student.getCgpa())
            .bloodGroup(student.getBloodGroup())
            .emergencyContact(student.getEmergencyContact())
            .departmentId(student.getDepartment() != null ? student.getDepartment().getId() : null)
            .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : null)
            .createdAt(student.getCreatedAt())
            .updatedAt(student.getUpdatedAt())
            .build();
    }
}
