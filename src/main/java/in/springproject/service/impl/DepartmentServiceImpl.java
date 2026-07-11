package in.springproject.service.impl;

import in.springproject.dto.department.*;
import in.springproject.entity.Department;
import in.springproject.entity.Teacher;
import in.springproject.exception.*;
import in.springproject.repository.*;
import in.springproject.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DepartmentService}.
 * <p>
 * Manages department lifecycle including creation, update, soft delete,
 * and aggregation of student, teacher, and course counts per department.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    /**
     * {@inheritDoc}
     * <p>Department code is stored in upper-case and must be unique.</p>
     */
    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Department", "code", request.getCode());
        }

        Department department = Department.builder()
            .name(request.getName())
            .code(request.getCode().toUpperCase())
            .description(request.getDescription())
            .establishedYear(request.getEstablishedYear())
            .build();

        if (request.getHeadTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getHeadTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getHeadTeacherId()));
            department.setHeadTeacher(teacher);
        }

        Department saved = departmentRepository.save(department);
        log.info("Created department: {} ({})", saved.getName(), saved.getCode());
        return mapToResponse(saved);
    }

    /**
     * {@inheritDoc}
     * <p>The head teacher assignment is optional; passing {@code null} leaves the existing one unchanged.</p>
     */
    @Override
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = getDepartmentEntityById(id);
        department.setName(request.getName());
        department.setCode(request.getCode().toUpperCase());
        department.setDescription(request.getDescription());
        department.setEstablishedYear(request.getEstablishedYear());

        if (request.getHeadTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getHeadTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getHeadTeacherId()));
            department.setHeadTeacher(teacher);
        }

        return mapToResponse(departmentRepository.save(department));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        return mapToResponse(getDepartmentEntityById(id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAllActive()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>Marks the department as deleted (soft delete). Actual records are not removed.</p>
     */
    @Override
    public void deleteDepartment(Long id) {
        Department department = getDepartmentEntityById(id);
        department.setDeleted(true);
        departmentRepository.save(department);
        log.info("Soft deleted department: {} ({})", department.getName(), id);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public long countDepartments() {
        return departmentRepository.countActive();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Loads a non-deleted {@link Department} by ID or throws {@link ResourceNotFoundException}.
     *
     * @param id the department's primary key
     * @return the department entity
     */
    private Department getDepartmentEntityById(Long id) {
        return departmentRepository.findById(id)
            .filter(d -> !Boolean.TRUE.equals(d.getDeleted()))
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    /**
     * Maps a {@link Department} entity to its corresponding {@link DepartmentResponse} DTO,
     * including live counts of students, teachers, and courses.
     *
     * @param dept the entity to map
     * @return the populated response DTO
     */
    private DepartmentResponse mapToResponse(Department dept) {
        return DepartmentResponse.builder()
            .id(dept.getId())
            .name(dept.getName())
            .code(dept.getCode())
            .description(dept.getDescription())
            .establishedYear(dept.getEstablishedYear())
            .headTeacherId(dept.getHeadTeacher() != null ? dept.getHeadTeacher().getId() : null)
            .headTeacherName(dept.getHeadTeacher() != null
                ? dept.getHeadTeacher().getFirstName() + " " + dept.getHeadTeacher().getLastName()
                : null)
            .studentCount(studentRepository.countByDepartmentId(dept.getId()))
            .teacherCount((long) teacherRepository.findByDepartmentId(dept.getId()).size())
            .courseCount((long) courseRepository.findByDepartmentId(dept.getId()).size())
            .createdAt(dept.getCreatedAt())
            .build();
    }
}
