package in.springproject.dto.course;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for creating or updating a course.
 * Credits must be between 1 and 10 (inclusive).
 */
@Data
public class CourseRequest {

    @NotBlank(message = "Course code is required")
    private String code;

    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 100)
    private String name;

    private String description;

    @NotNull(message = "Credits is required")
    @Min(value = 1)
    @Max(value = 10)
    private Integer credits;

    private Integer maxStudents;

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
