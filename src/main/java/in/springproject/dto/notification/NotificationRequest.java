ackage in.springproject.dto.notification;

import in.springproject.entity.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for creating a notification.
 * When {@code recipientId} is null and {@code isBroadcast} is true,
 * the notification is sent to all users.
 */
@Data
public class NotificationRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Type is required")
    private NotificationType type;

    /** Target user ID; {@code null} indicates a broadcast notification. */
    private Long recipientId;

    private Boolean isBroadcast;
}
