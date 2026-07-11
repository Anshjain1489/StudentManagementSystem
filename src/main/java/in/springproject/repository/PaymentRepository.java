package in.springproject.repository;

import in.springproject.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Payment} entity operations.
 *
 * <p>Provides receipt-number lookup, student payment history,
 * total-collection aggregates (overall and time-windowed), and
 * paginated active-payment listing.</p>
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Finds a payment by its unique receipt number.
     *
     * @param receiptNumber the receipt number to search
     * @return an {@link Optional} containing the payment if found
     */
    Optional<Payment> findByReceiptNumber(String receiptNumber);

    /**
     * Returns all payments made against a specific fee record.
     *
     * @param feesId the fees ID
     * @return list of payments for the fee
     */
    List<Payment> findByFeesId(Long feesId);

    /**
     * Returns all payments made by a specific student (navigated via the fees association).
     *
     * @param studentId the student ID
     * @return list of payments by the student
     */
    List<Payment> findByFeesStudentId(Long studentId);

    /**
     * Calculates the total amount collected from all completed payments.
     *
     * @return total collected amount as {@link BigDecimal}, or {@code null} if none exist
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal findTotalCollected();

    /**
     * Calculates the total amount collected from completed payments within a date-time window.
     *
     * @param start the start of the window (inclusive)
     * @param end   the end of the window (inclusive)
     * @return total collected amount within the window, or {@code null} if none exist
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal findTotalCollectedBetween(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    /**
     * Returns a paginated, date-descending list of all non-deleted payments.
     *
     * @param pageable pagination parameters
     * @return page of active payments ordered by payment date descending
     */
    @Query("SELECT p FROM Payment p WHERE p.deleted = false ORDER BY p.paymentDate DESC")
    Page<Payment> findAllActive(Pageable pageable);
}
