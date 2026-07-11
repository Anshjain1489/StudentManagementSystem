ackage in.springproject.dto.fees;

import in.springproject.entity.enums.FeeType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for raising a fee record against a student.
 * Amount must be a positive decimal; due date is mandatory.
 */
@Data
public class FeesRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Fee type is required")
    private FeeType feeType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private String academicYear;
    private String semester;
    private String description;
}
