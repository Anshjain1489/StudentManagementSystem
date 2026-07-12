package in.springproject.repository;

import in.springproject.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Department} entity operations.
 *
 * <p>Provides lookups by department code and name, soft-delete aware queries,
 * and an active-count aggregate.</p>
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Finds a department by its unique code (e.g., "CS", "EE").
     *
     * @param code the department code
     * @return an {@link Optional} containing the department if found
     */
    Optional<Department> findByCode(String code);

    /**
     * Finds a department by its full name.
     *
     * @param name the department name
     * @return an {@link Optional} containing the department if found
     */
    Optional<Department> findByName(String name);

    /**
     * Checks whether a department with the given code already exists.
     *
     * @param code the department code to check
     * @return {@code true} if the code is already in use
     */
    boolean existsByCode(String code);

    /**
     * Checks whether a department with the given name already exists.
     *
     * @param name the department name to check
     * @return {@code true} if the name is already in use
     */
    boolean existsByName(String name);

    /**
     * Returns all non-deleted departments.
     *
     * @return list of active departments
     */
    @Query("SELECT d FROM Department d WHERE d.deleted = false")
    List<Department> findAllActive();

    /**
     * Counts all non-deleted departments.
     *
     * @return count of active departments
     */
    @Query("SELECT COUNT(d) FROM Department d WHERE d.deleted = false")
    long countActive();
}
