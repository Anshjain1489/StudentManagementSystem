package in.springproject.dto.department;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for creating or updating a department.
 * Optionally links a head teacher by ID.
 */
@Data
public class DepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Department code is required")
    @Size(min = 2, max = 20)
    private String code;

    private String description;
    private Integer establishedYear;

    /** Optional ID of the teacher designated as department head. */
    private Long headTeacherId;
}
