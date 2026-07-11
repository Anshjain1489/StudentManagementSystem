ackage in.springproject.repository;

import in.springproject.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Semester} entity operations.
 *
 * <p>Provides lookups for the currently active semester, academic-year filtering,
 * and a soft-delete aware list ordered by start date.</p>
 */
@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    /**
     * Finds the currently active semester.
     * Only one semester should have {@code isActive = true} at any time.
     *
     * @return an {@link Optional} containing the active semester if one exists
     */
    Optional<Semester> findByIsActiveTrue();

    /**
     * Returns all semesters for a given academic year (e.g., "2024-2025").
     *
     * @param academicYear the academic year string
     * @return list of semesters in the academic year
     */
    List<Semester> findByAcademicYear(String academicYear);

    /**
     * Returns all non-deleted semesters ordered by start date descending
     * (most recent first).
     *
     * @return list of active semesters
     */
    @Query("SELECT s FROM Semester s WHERE s.deleted = false ORDER BY s.startDate DESC")
    List<Semester> findAllActive();
}
