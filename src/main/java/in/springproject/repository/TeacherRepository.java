ackage in.springproject.repository;

import in.springproject.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Teacher} entity operations.
 *
 * <p>Provides soft-delete aware queries, pagination, full-text search,
 * and department-based filtering for teachers.</p>
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * Finds a teacher by their unique employee ID.
     *
     * @param employeeId the employee ID to search
     * @return an {@link Optional} containing the teacher if found
     */
    Optional<Teacher> findByEmployeeId(String employeeId);

    /**
     * Finds a teacher by their email address.
     *
     * @param email the email address to search
     * @return an {@link Optional} containing the teacher if found
     */
    Optional<Teacher> findByEmail(String email);

    /**
     * Checks whether a teacher with the given employee ID already exists.
     *
     * @param employeeId the employee ID to check
     * @return {@code true} if the employee ID is already in use
     */
    boolean existsByEmployeeId(String employeeId);

    /**
     * Checks whether a teacher with the given email already exists.
     *
     * @param email the email to check
     * @return {@code true} if the email is already in use
     */
    boolean existsByEmail(String email);

    /**
     * Returns all non-deleted teachers as a flat list.
     *
     * @return list of active teachers
     */
    @Query("SELECT t FROM Teacher t WHERE t.deleted = false")
    List<Teacher> findAllActive();

    /**
     * Returns a paginated list of all non-deleted teachers.
     *
     * @param pageable pagination and sorting parameters
     * @return page of active teachers
     */
    @Query("SELECT t FROM Teacher t WHERE t.deleted = false")
    Page<Teacher> findAllActive(Pageable pageable);

    /**
     * Returns all non-deleted teachers belonging to a specific department.
     *
     * @param deptId the department ID
     * @return list of teachers in the given department
     */
    @Query("SELECT t FROM Teacher t WHERE t.deleted = false AND t.department.id = :deptId")
    List<Teacher> findByDepartmentId(@Param("deptId") Long deptId);

    /**
     * Full-text search across first name, last name, email, and employee ID
     * for non-deleted teachers.
     *
     * @param query    the search term
     * @param pageable pagination and sorting parameters
     * @return page of matching teachers
     */
    @Query("SELECT t FROM Teacher t WHERE t.deleted = false AND " +
           "(LOWER(t.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.email) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "t.employeeId LIKE CONCAT('%', :q, '%'))")
    Page<Teacher> searchTeachers(@Param("q") String query, Pageable pageable);

    /**
     * Counts all non-deleted teachers.
     *
     * @return count of active teachers
     */
    @Query("SELECT COUNT(t) FROM Teacher t WHERE t.deleted = false")
    long countActive();
}
