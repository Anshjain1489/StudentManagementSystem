package in.springproject.service.impl;

import in.springproject.dto.course.*;
import in.springproject.entity.*;
import in.springproject.exception.*;
import in.springproject.repository.*;
import in.springproject.service.CourseService;
import in.springproject.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CourseService}.
 * Manages course CRUD, teacher assignment, and enrollment tracking.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Course", "code", request.getCode());
        }
        Department dept = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        Course course = Course.builder()
            .code(request.getCode().toUpperCase())
            .name(request.getName())
            .description(request.getDescription())
            .credits(request.getCredits())
            .maxStudents(request.getMaxStudents())
            .department(dept)
            .build();

        Course saved = courseRepository.save(course);
        log.info("Created course: {} ({})", saved.getName(), saved.getCode());
        return mapToResponse(saved);
    }

    @Override
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = getCourseEntityById(id);
        Department dept = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        course.setCode(request.getCode().toUpperCase());
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());
        course.setMaxStudents(request.getMaxStudents());
        course.setDepartment(dept);
        return mapToResponse(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        return mapToResponse(getCourseEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getAllCourses(Pageable pageable) {
        Page<CourseResponse> page = courseRepository.findAllActive(pageable).map(this::mapToResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = getCourseEntityById(id);
        course.setDeleted(true);
        courseRepository.save(course);
        log.info("Soft deleted course: {}", id);
    }

    @Override
    public void assignTeacher(Long courseId, Long teacherId) {
        Course course = getCourseEntityById(courseId);
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));
        teacher.getCourses().add(course);
        teacherRepository.save(teacher);
        log.info("Assigned teacher {} to course {}", teacherId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCourses() {
        return courseRepository.countActive();
    }

    // ─── Private Helpers ───────────────────────────────────────────────────────

    private Course getCourseEntityById(Long id) {
        return courseRepository.findById(id)
            .filter(c -> !Boolean.TRUE.equals(c.getDeleted()))
            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    private CourseResponse mapToResponse(Course c) {
        return CourseResponse.builder()
            .id(c.getId())
            .code(c.getCode())
            .name(c.getName())
            .description(c.getDescription())
            .credits(c.getCredits())
            .maxStudents(c.getMaxStudents())
            .isActive(c.getIsActive())
            .departmentId(c.getDepartment() != null ? c.getDepartment().getId() : null)
            .departmentName(c.getDepartment() != null ? c.getDepartment().getName() : null)
            .enrolledStudents(courseRepository.countEnrolledStudents(c.getId()))
            .createdAt(c.getCreatedAt())
            .build();
    }
}
