package in.springproject.repository;

import in.springproject.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for {@link Exam} entity operations.
 *
 * <p>Provides lookups by course, semester, date range, and student-specific
 * exam queries (via course enrollment), as well as paginated active-exam listing.</p>
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    /**
     * Returns all exams for a specific course.
     *
     * @param courseId the course ID
     * @return list of exams for the course
     */
    List<Exam> findByCourseId(Long courseId);

    /**
     * Returns all exams for a specific semester.
     *
     * @param semesterId the semester ID
     * @return list of exams in the semester
     */
    List<Exam> findBySemesterId(Long semesterId);

    /**
     * Returns all exams scheduled within an inclusive date range.
     *
     * @param start the start date (inclusive)
     * @param end   the end date (inclusive)
     * @return list of exams within the date range
     */
    @Query("SELECT e FROM Exam e WHERE e.examDate BETWEEN :start AND :end")
    List<Exam> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    /**
     * Returns a paginated list of all non-deleted exams.
     *
     * @param pageable pagination and sorting parameters
     * @return page of active exams
     */
    @Query("SELECT e FROM Exam e WHERE e.deleted = false")
    Page<Exam> findAllActive(Pageable pageable);

    /**
     * Returns all non-deleted exams for courses in which a specific student is enrolled.
     *
     * @param studentId the student ID
     * @return list of exams relevant to the student
     */
    @Query("SELECT e FROM Exam e JOIN e.course c JOIN c.students s WHERE s.id = :studentId AND e.deleted = false")
    List<Exam> findExamsByStudentId(@Param("studentId") Long studentId);
}
