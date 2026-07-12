package in.springproject.repository;

import in.springproject.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Course} entity operations.
 *
 * <p>Provides soft-delete aware queries, department-based filtering,
 * pagination, and an enrolled-student count aggregate.</p>
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds a course by its unique course code.
     *
     * @param code the course code (e.g., "CS101")
     * @return an {@link Optional} containing the course if found
     */
    Optional<Course> findByCode(String code);

    /**
     * Checks whether a course with the given code already exists.
     *
     * @param code the course code to check
     * @return {@code true} if the code is already in use
     */
    boolean existsByCode(String code);

    /**
     * Returns all non-deleted courses as a flat list.
     *
     * @return list of active courses
     */
    @Query("SELECT c FROM Course c WHERE c.deleted = false")
    List<Course> findAllActive();

    /**
     * Returns a paginated list of all non-deleted courses.
     *
     * @param pageable pagination and sorting parameters
     * @return page of active courses
     */
    @Query("SELECT c FROM Course c WHERE c.deleted = false")
    Page<Course> findAllActive(Pageable pageable);

    /**
     * Returns all non-deleted courses belonging to a specific department.
     *
     * @param deptId the department ID
     * @return list of active courses in the given department
     */
    @Query("SELECT c FROM Course c WHERE c.deleted = false AND c.department.id = :deptId")
    List<Course> findByDepartmentId(@Param("deptId") Long deptId);

    /**
     * Counts all non-deleted courses.
     *
     * @return count of active courses
     */
    @Query("SELECT COUNT(c) FROM Course c WHERE c.deleted = false")
    long countActive();

    /**
     * Counts the distinct number of students enrolled in a specific course.
     *
     * @param courseId the course ID
     * @return number of enrolled students
     */
    @Query("SELECT COUNT(DISTINCT sc.student) FROM Course c JOIN c.students sc WHERE c.id = :courseId")
    long countEnrolledStudents(@Param("courseId") Long courseId);
}
