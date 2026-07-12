package in.springproject.entity;

import in.springproject.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;
    @Column(name = "is_broadcast", nullable = false)
    @Builder.Default
    private Boolean isBroadcast = false;
}
