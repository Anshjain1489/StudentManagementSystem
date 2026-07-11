ackage in.springproject.dto.fees;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.springproject.entity.enums.PaymentMethod;
import in.springproject.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing a payment record returned by API responses.
 * Includes receipt number and transaction ID for audit purposes.
 * Null fields are excluded from JSON serialization via {@link JsonInclude}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private Long id;
    private Long feesId;
    private String studentName;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String receiptNumber;
    private String transactionId;
    private String remarks;
}
