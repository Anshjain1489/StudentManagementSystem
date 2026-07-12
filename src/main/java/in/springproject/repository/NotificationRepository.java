package in.springproject.repository;

import in.springproject.entity.Notification;
import in.springproject.entity.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Notification} entity operations.
 *
 * <p>Supports both targeted (per-user recipient) and broadcast notifications.
 * Provides unread count, bulk mark-all-read, and type-based pagination.</p>
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Returns a paginated, date-descending list of non-deleted notifications
     * visible to a specific user. Includes both notifications directly
     * addressed to the user and system-wide broadcast notifications.
     *
     * @param userId   the recipient user ID
     * @param pageable pagination parameters
     * @return page of notifications for the user
     */
    @Query("SELECT n FROM Notification n WHERE (n.recipient.id = :userId OR n.isBroadcast = true) " +
           "AND n.deleted = false ORDER BY n.createdAt DESC")
    Page<Notification> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Counts unread non-deleted notifications visible to a specific user,
     * including both direct and broadcast notifications.
     *
     * @param userId the recipient user ID
     * @return count of unread notifications
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.recipient.id = :userId OR n.isBroadcast = true) " +
           "AND n.isRead = false AND n.deleted = false")
    long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * Bulk-marks all unread notifications visible to a specific user as read.
     * Includes both direct and broadcast notifications.
     *
     * @param userId the recipient user ID
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE (n.recipient.id = :userId OR n.isBroadcast = true) " +
           "AND n.isRead = false")
    void markAllReadByUserId(@Param("userId") Long userId);

    /**
     * Returns a paginated list of notifications filtered by type
     * (e.g., ANNOUNCEMENT, REMINDER, ALERT).
     *
     * @param type     the notification type
     * @param pageable pagination parameters
     * @return page of notifications of the given type
     */
    Page<Notification> findByType(NotificationType type, Pageable pageable);
}
