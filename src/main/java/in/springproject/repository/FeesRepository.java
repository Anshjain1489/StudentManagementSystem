package in.springproject.repository;

import in.springproject.entity.Fees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for {@link Fees} entity operations.
 *
 * <p>Provides queries for overdue fees, pending amount aggregates,
 * due-date window lookups, and paginated active-fees listing.</p>
 */
@Repository
public interface FeesRepository extends JpaRepository<Fees, Long> {

    /**
     * Returns all fee records for a specific student.
     *
     * @param studentId the student ID
     * @return list of fee records for the student
     */
    List<Fees> findByStudentId(Long studentId);

    /**
     * Returns fee records for a student filtered by payment status.
     *
     * @param studentId the student ID
     * @param isPaid    {@code true} for paid fees, {@code false} for unpaid
     * @return list of fee records matching the payment status
     */
    List<Fees> findByStudentIdAndIsPaid(Long studentId, Boolean isPaid);

    /**
     * Returns all unpaid fees whose due date has already passed.
     *
     * @param today the current date used as the overdue threshold
     * @return list of overdue fee records
     */
    @Query("SELECT f FROM Fees f WHERE f.isPaid = false AND f.dueDate < :today")
    List<Fees> findOverdueFees(@Param("today") LocalDate today);

    /**
     * Calculates the total pending (unpaid) amount for a specific student.
     *
     * @param studentId the student ID
     * @return sum of unpaid amounts as {@link BigDecimal}, or {@code null} if none exist
     */
    @Query("SELECT SUM(f.amount) FROM Fees f WHERE f.student.id = :studentId AND f.isPaid = false")
    BigDecimal findPendingAmountByStudent(@Param("studentId") Long studentId);

    /**
     * Counts the total number of unpaid fee records across all students.
     *
     * @return count of pending fee records
     */
    @Query("SELECT COUNT(f) FROM Fees f WHERE f.isPaid = false")
    long countPending();

    /**
     * Returns a paginated list of all non-deleted fee records.
     *
     * @param pageable pagination and sorting parameters
     * @return page of active fee records
     */
    @Query("SELECT f FROM Fees f WHERE f.deleted = false")
    Page<Fees> findAllActive(Pageable pageable);

    /**
     * Returns all unpaid fees with due dates falling within an inclusive date range.
     * Useful for generating upcoming-payment reminders.
     *
     * @param start the start of the due-date range (inclusive)
     * @param end   the end of the due-date range (inclusive)
     * @return list of unpaid fees due within the range
     */
    @Query("SELECT f FROM Fees f WHERE f.dueDate BETWEEN :start AND :end AND f.isPaid = false")
    List<Fees> findDueBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
