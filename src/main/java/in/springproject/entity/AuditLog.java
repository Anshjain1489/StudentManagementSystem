ackage in.springproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "action", nullable = false)
    private String action;
    @Column(name = "entity_name", nullable = false)
    private String entityName;
    @Column(name = "entity_id")
    private Long entityId;
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
    @Column(name = "performed_by")
    private String performedBy;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
}
