ackage in.springproject.dto.exam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a student's exam result returned by API responses.
 * Includes calculated grade, GPA, pass/fail status, and percentage.
 * Null fields are excluded from JSON serialization via {@link JsonInclude}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private Long examId;
    private String examName;
    private String courseName;
    private Double marksObtained;
    private Integer totalMarks;
    private String grade;
    private Double gpa;
    private Boolean isPass;
    private String remarks;
    private Double percentage;
}
