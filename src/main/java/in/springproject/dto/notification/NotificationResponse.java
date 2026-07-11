package in.springproject.dto.notification;

import in.springproject.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing a notification returned by API responses.
 * Includes read status to support notification inbox functionality.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private Boolean isBroadcast;
    private LocalDateTime createdAt;
}
