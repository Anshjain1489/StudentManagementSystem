package in.springproject.dto.exam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO representing an exam record returned by API responses.
 * Includes denormalized course, semester, and classroom details.
 * Null fields are excluded from JSON serialization via {@link JsonInclude}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamResponse {

    private Long id;
    private String name;
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer totalMarks;
    private Integer passingMarks;
    private String description;
    private Long courseId;
    private String courseName;
    private Long semesterId;
    private String semesterName;
    private Long classroomId;
    private String classroomNumber;
    private LocalDateTime createdAt;
}
