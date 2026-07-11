ackage in.springproject.dto.department;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing a department returned by API responses.
 * Includes aggregated counts for students, teachers and courses.
 * Null fields are excluded from JSON serialization via {@link JsonInclude}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer establishedYear;
    private Long headTeacherId;
    private String headTeacherName;
    private Long studentCount;
    private Long teacherCount;
    private Long courseCount;
    private LocalDateTime createdAt;
}
