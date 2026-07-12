package in.springproject.repository;

import in.springproject.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Student} entity operations.
 *
 * <p>Extends {@link JpaSpecificationExecutor} to support dynamic filtering via
 * the Specification pattern. Includes soft-delete aware queries, pagination,
 * full-text search, and department-based filtering.</p>
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    /**
     * Finds a student by their unique roll number.
     *
     * @param rollNumber the roll number to search
     * @return an {@link Optional} containing the student if found
     */
    Optional<Student> findByRollNumber(String rollNumber);

    /**
     * Finds a student by their email address.
     *
     * @param email the email address to search
     * @return an {@link Optional} containing the student if found
     */
    Optional<Student> findByEmail(String email);

    /**
     * Checks whether a student with the given roll number already exists.
     *
     * @param rollNumber the roll number to check
     * @return {@code true} if the roll number is already in use
     */
    boolean existsByRollNumber(String rollNumber);

    /**
     * Checks whether a student with the given email already exists.
     *
     * @param email the email to check
     * @return {@code true} if the email is already in use
     */
    boolean existsByEmail(String email);

    /**
     * Returns all non-deleted students as a flat list.
     *
     * @return list of active students
     */
    @Query("SELECT s FROM Student s WHERE s.deleted = false")
    List<Student> findAllActive();

    /**
     * Returns a paginated list of all non-deleted students.
     *
     * @param pageable pagination and sorting parameters
     * @return page of active students
     */
    @Query("SELECT s FROM Student s WHERE s.deleted = false")
    Page<Student> findAllActive(Pageable pageable);

    /**
     * Returns a paginated list of non-deleted students belonging to a specific department.
     *
     * @param deptId   the department ID
     * @param pageable pagination and sorting parameters
     * @return page of students in the given department
     */
    @Query("SELECT s FROM Student s WHERE s.deleted = false AND s.department.id = :deptId")
    Page<Student> findByDepartmentId(@Param("deptId") Long deptId, Pageable pageable);

    /**
     * Full-text search across first name, last name, email, and roll number
     * for non-deleted students.
     *
     * @param query    the search term
     * @param pageable pagination and sorting parameters
     * @return page of matching students
     */
    @Query("SELECT s FROM Student s WHERE s.deleted = false AND " +
           "(LOWER(s.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "s.rollNumber LIKE CONCAT('%', :q, '%'))")
    Page<Student> searchStudents(@Param("q") String query, Pageable pageable);

    /**
     * Counts all non-deleted students.
     *
     * @return count of active students
     */
    @Query("SELECT COUNT(s) FROM Student s WHERE s.deleted = false")
    long countActive();

    /**
     * Counts non-deleted students in a specific department.
     *
     * @param deptId the department ID
     * @return count of active students in the department
     */
    @Query("SELECT COUNT(s) FROM Student s WHERE s.deleted = false AND s.department.id = :deptId")
    long countByDepartmentId(@Param("deptId") Long deptId);
}
