package in.springproject.dto.fees;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.springproject.entity.enums.FeeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing a fee record returned by API responses.
 * Includes student name and roll number for display.
 * Null fields are excluded from JSON serialization via {@link JsonInclude}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeesResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private FeeType feeType;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String academicYear;
    private String semester;
    private String description;
    private Boolean isPaid;
    private LocalDateTime createdAt;
}
