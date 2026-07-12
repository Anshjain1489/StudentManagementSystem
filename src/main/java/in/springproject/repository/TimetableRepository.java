package in.springproject.repository;

import in.springproject.entity.Timetable;
import in.springproject.entity.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Timetable} entity operations.
 *
 * <p>Provides scheduling lookups by course, teacher, day, semester, student,
 * and classroom. Distinguishes between all schedules and active-semester schedules
 * to support timetable views for students and teachers.</p>
 */
@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    /**
     * Returns all timetable entries for a specific course.
     *
     * @param courseId the course ID
     * @return list of timetable entries for the course
     */
    List<Timetable> findByCourseId(Long courseId);

    /**
     * Returns all timetable entries assigned to a specific teacher.
     *
     * @param teacherId the teacher ID
     * @return list of timetable entries for the teacher
     */
    List<Timetable> findByTeacherId(Long teacherId);

    /**
     * Returns all timetable entries scheduled on a specific day of the week.
     *
     * @param dayOfWeek the day of the week
     * @return list of timetable entries for the given day
     */
    List<Timetable> findByDayOfWeek(DayOfWeek dayOfWeek);

    /**
     * Returns all timetable entries for a specific semester.
     *
     * @param semesterId the semester ID
     * @return list of timetable entries in the semester
     */
    List<Timetable> findBySemesterId(Long semesterId);

    /**
     * Returns non-deleted timetable entries for the active semester
     * that belong to courses in which the specified student is enrolled.
     *
     * @param studentId the student ID
     * @return list of active timetable entries relevant to the student
     */
    @Query("SELECT t FROM Timetable t WHERE t.deleted = false AND t.semester.isActive = true AND t.course.id IN " +
           "(SELECT c.id FROM Course c JOIN c.students s WHERE s.id = :studentId)")
    List<Timetable> findByStudentId(@Param("studentId") Long studentId);

    /**
     * Returns non-deleted timetable entries for the active semester
     * assigned to the specified teacher.
     *
     * @param teacherId the teacher ID
     * @return list of active timetable entries for the teacher
     */
    @Query("SELECT t FROM Timetable t WHERE t.deleted = false AND t.semester.isActive = true AND t.teacher.id = :teacherId")
    List<Timetable> findActiveByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * Returns non-deleted timetable entries for a classroom on a specific day.
     * Useful for detecting scheduling conflicts.
     *
     * @param classroomId the classroom ID
     * @param day         the day of the week
     * @return list of timetable entries for the classroom on the given day
     */
    @Query("SELECT t FROM Timetable t WHERE t.deleted = false AND t.classroom.id = :classroomId AND t.dayOfWeek = :day")
    List<Timetable> findByClassroomAndDay(@Param("classroomId") Long classroomId, @Param("day") DayOfWeek day);
}
