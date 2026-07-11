ackage in.springproject.dto.course;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing a course returned by API responses.
 * Includes current enrollment count and department name.
 * Null fields are excluded from JSON serialization via {@link JsonInclude}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private Integer maxStudents;
    private Boolean isActive;
    private Long departmentId;
    private String departmentName;
    private Long enrolledStudents;
    private LocalDateTime createdAt;
}
