ackage in.springproject.service;

import in.springproject.dto.course.*;
import in.springproject.util.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Course management operations.
 * Covers CRUD, pagination, department filtering, and teacher assignment.
 */
public interface CourseService {

    /**
     * Creates a new course.
     *
     * @param request course data including department association
     * @return created course details
     */
    CourseResponse createCourse(CourseRequest request);

    /**
     * Updates an existing course's information.
     *
     * @param id      the course's primary key
     * @param request updated course data
     * @return updated course details
     */
    CourseResponse updateCourse(Long id, CourseRequest request);

    /**
     * Retrieves a course by its primary key.
     *
     * @param id the course's primary key
     * @return course details
     */
    CourseResponse getCourseById(Long id);

    /**
     * Returns a paginated list of all active courses.
     *
     * @param pageable pagination and sorting parameters
     * @return paginated course list
     */
    PageResponse<CourseResponse> getAllCourses(Pageable pageable);

    /**
     * Returns all courses belonging to a specific department.
     *
     * @param departmentId the department's primary key
     * @return list of course details
     */
    List<CourseResponse> getCoursesByDepartment(Long departmentId);

    /**
     * Soft-deletes a course record.
     *
     * @param id the course's primary key
     */
    void deleteCourse(Long id);

    /**
     * Assigns a teacher to the specified course.
     *
     * @param courseId  the course's primary key
     * @param teacherId the teacher's primary key
     */
    void assignTeacher(Long courseId, Long teacherId);

    /**
     * Returns the total count of active (non-deleted) courses.
     *
     * @return count of active courses
     */
    long countCourses();
}
