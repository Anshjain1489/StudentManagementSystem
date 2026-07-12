package in.springproject.dto.exam;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for submitting or updating a student's exam result.
 * The marks obtained must be a non-negative decimal value.
 */
@Data
public class ResultRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotNull(message = "Marks obtained is required")
    @DecimalMin(value = "0.0")
    private Double marksObtained;

    private String remarks;
}
