package in.springproject.service.impl;

import in.springproject.dto.notification.*;
import in.springproject.entity.Notification;
import in.springproject.entity.User;
import in.springproject.exception.ResourceNotFoundException;
import in.springproject.repository.NotificationRepository;
import in.springproject.repository.UserRepository;
import in.springproject.service.NotificationService;
import in.springproject.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link NotificationService}.
 * Supports both targeted and broadcast notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification.NotificationBuilder builder = Notification.builder()
            .title(request.getTitle())
            .message(request.getMessage())
            .type(request.getType())
            .isBroadcast(Boolean.TRUE.equals(request.getIsBroadcast()));

        if (request.getRecipientId() != null) {
            User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getRecipientId()));
            builder.recipient(recipient);
        }

        return mapToResponse(notificationRepository.save(builder.build()));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        Page<NotificationResponse> page = notificationRepository
            .findByUserId(userId, pageable).map(this::mapToResponse);
        return PageResponse.from(page);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        n.setIsRead(true);
        notificationRepository.save(n);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllReadByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification n = notificationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        n.setDeleted(true);
        notificationRepository.save(n);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
            .id(n.getId()).title(n.getTitle()).message(n.getMessage())
            .type(n.getType()).isRead(n.getIsRead()).isBroadcast(n.getIsBroadcast())
            .createdAt(n.getCreatedAt())
            .build();
    }
}
