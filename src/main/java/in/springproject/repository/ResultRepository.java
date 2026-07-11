package in.springproject.repository;

import in.springproject.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Result} entity operations.
 *
 * <p>Provides lookups by student/exam combination, semester-scoped result retrieval,
 * average marks calculation per exam, and CGPA computation per student.</p>
 */
@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    /**
     * Finds the result for a specific student in a specific exam.
     * Enforces the one-result-per-student-per-exam constraint at query level.
     *
     * @param studentId the student ID
     * @param examId    the exam ID
     * @return an {@link Optional} containing the result if found
     */
    Optional<Result> findByStudentIdAndExamId(Long studentId, Long examId);

    /**
     * Returns all results for a specific student across all exams.
     *
     * @param studentId the student ID
     * @return list of results for the student
     */
    List<Result> findByStudentId(Long studentId);

    /**
     * Returns all results for a specific exam across all students.
     *
     * @param examId the exam ID
     * @return list of results for the exam
     */
    List<Result> findByExamId(Long examId);

    /**
     * Calculates the average marks obtained in a specific exam.
     *
     * @param examId the exam ID
     * @return average marks as a {@link Double}, or {@code null} if no results exist
     */
    @Query("SELECT AVG(r.marksObtained) FROM Result r WHERE r.exam.id = :examId")
    Double findAverageMarksByExam(@Param("examId") Long examId);

    /**
     * Returns all results for a student within a specific semester.
     *
     * @param studentId  the student ID
     * @param semesterId the semester ID
     * @return list of results for the student in the semester
     */
    @Query("SELECT r FROM Result r WHERE r.student.id = :studentId AND r.exam.semester.id = :semesterId")
    List<Result> findByStudentAndSemester(@Param("studentId") Long studentId,
                                          @Param("semesterId") Long semesterId);

    /**
     * Calculates the Cumulative Grade Point Average (CGPA) for a student
     * by averaging all individual GPA values across their results.
     *
     * @param studentId the student ID
     * @return CGPA as a {@link Double}, or {@code null} if no results exist
     */
    @Query("SELECT AVG(r.gpa) FROM Result r WHERE r.student.id = :studentId")
    Double calculateCgpa(@Param("studentId") Long studentId);
}
