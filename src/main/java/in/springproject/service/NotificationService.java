package in.springproject.service;

import in.springproject.dto.notification.*;
import in.springproject.util.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing in-app notifications.
 */
public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);
    PageResponse<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    long countUnread(Long userId);
    void deleteNotification(Long id);
}
