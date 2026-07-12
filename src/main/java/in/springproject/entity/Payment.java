package in.springproject.entity;

import in.springproject.entity.enums.PaymentMethod;
import in.springproject.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fees_id", nullable = false)
    private Fees fees;
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
    @Column(name = "receipt_number", unique = true)
    private String receiptNumber;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "remarks")
    private String remarks;
}
