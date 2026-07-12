package in.springproject.repository;

import in.springproject.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for {@link AuditLog} entity operations.
 *
 * <p>Supports three primary audit query patterns: looking up all events for a
 * specific entity instance, paginated events by the user who performed them,
 * and time-range based event retrieval for audit reporting.</p>
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Returns all audit log entries for a specific entity instance.
     * Useful for displaying the change history of a single record.
     *
     * @param entityName the simple class name of the entity (e.g., "Student")
     * @param entityId   the primary key of the entity instance
     * @return list of audit log entries for the entity
     */
    List<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId);

    /**
     * Returns a paginated list of audit log entries performed by a specific user.
     *
     * @param performedBy the username or identifier of the actor
     * @param pageable    pagination and sorting parameters
     * @return page of audit log entries by the user
     */
    Page<AuditLog> findByPerformedBy(String performedBy, Pageable pageable);

    /**
     * Returns all audit log entries whose timestamps fall within an inclusive range.
     * Useful for generating compliance or activity reports for a given period.
     *
     * @param start the start of the time range (inclusive)
     * @param end   the end of the time range (inclusive)
     * @return list of audit log entries within the range
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
