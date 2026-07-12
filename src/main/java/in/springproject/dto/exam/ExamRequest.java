package in.springproject.dto.exam;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for scheduling an exam.
 * Links the exam to a course and optionally to a semester and classroom.
 */
@Data
public class ExamRequest {

    @NotBlank(message = "Exam name is required")
    private String name;

    @NotNull(message = "Exam date is required")
    private LocalDate examDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @NotNull(message = "Total marks is required")
    @Min(value = 1)
    private Integer totalMarks;

    @NotNull(message = "Passing marks is required")
    private Integer passingMarks;

    private String description;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private Long semesterId;
    private Long classroomId;
}
