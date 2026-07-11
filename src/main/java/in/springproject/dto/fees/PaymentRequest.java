ackage in.springproject.dto.fees;

import in.springproject.entity.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for recording a payment against an existing fee record.
 * Supports multiple payment methods including online and cash.
 */
@Data
public class PaymentRequest {

    @NotNull(message = "Fees ID is required")
    private Long feesId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    /** External gateway transaction reference (optional for cash payments). */
    private String transactionId;

    private String remarks;
}
