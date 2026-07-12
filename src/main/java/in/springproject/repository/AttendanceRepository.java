package in.springproject.repository;

import in.springproject.entity.Attendance;
import in.springproject.entity.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Attendance} entity operations.
 *
 * <p>Provides lookups by student/course/date combinations, date-range filtering,
 * status-based count aggregates, and paginated student attendance history.</p>
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * Finds the attendance record for a specific student, course, and date.
     * Useful for duplicate-check before saving.
     *
     * @param studentId the student ID
     * @param courseId  the course ID
     * @param date      the attendance date
     * @return an {@link Optional} containing the record if it exists
     */
    Optional<Attendance> findByStudentIdAndCourseIdAndDate(Long studentId, Long courseId, LocalDate date);

    /**
     * Returns all attendance records for a specific student in a specific course.
     *
     * @param studentId the student ID
     * @param courseId  the course ID
     * @return list of attendance records
     */
    List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Returns attendance records for a student within an inclusive date range.
     *
     * @param studentId the student ID
     * @param startDate the start of the date range (inclusive)
     * @param endDate   the end of the date range (inclusive)
     * @return list of attendance records within the range
     */
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByStudentAndDateRange(@Param("studentId") Long studentId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * Returns all attendance records for a course on a specific date.
     *
     * @param courseId the course ID
     * @param date     the attendance date
     * @return list of attendance records for that course and date
     */
    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.date = :date")
    List<Attendance> findByCourseAndDate(@Param("courseId") Long courseId, @Param("date") LocalDate date);

    /**
     * Counts attendance records for a student in a course filtered by status
     * (e.g., PRESENT, ABSENT, LATE).
     *
     * @param studentId the student ID
     * @param courseId  the course ID
     * @param status    the attendance status to filter by
     * @return count of records matching the given status
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.course.id = :courseId AND a.status = :status")
    long countByStudentAndCourseAndStatus(@Param("studentId") Long studentId,
                                          @Param("courseId") Long courseId,
                                          @Param("status") AttendanceStatus status);

    /**
     * Counts all attendance records for a student in a course regardless of status.
     *
     * @param studentId the student ID
     * @param courseId  the course ID
     * @return total number of attendance records
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.course.id = :courseId")
    long countTotalByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * Returns a paginated, date-descending history of attendance records for a student.
     *
     * @param studentId the student ID
     * @param pageable  pagination parameters
     * @return page of attendance records ordered by date descending
     */
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId ORDER BY a.date DESC")
    Page<Attendance> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);
}
